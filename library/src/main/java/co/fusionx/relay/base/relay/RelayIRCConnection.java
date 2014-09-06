package co.fusionx.relay.base.relay;

import com.google.common.base.Function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

import co.fusionx.relay.base.ConnectionStatus;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.event.channel.ChannelConnectEvent;
import co.fusionx.relay.event.channel.ChannelDisconnectEvent;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelStopEvent;
import co.fusionx.relay.event.query.QueryConnectEvent;
import co.fusionx.relay.event.query.QueryDisconnectEvent;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.event.query.QueryStopEvent;
import co.fusionx.relay.event.server.ConnectEvent;
import co.fusionx.relay.event.server.ConnectingEvent;
import co.fusionx.relay.event.server.DisconnectEvent;
import co.fusionx.relay.event.server.ReconnectEvent;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.event.server.StopEvent;
import co.fusionx.relay.parser.connection.ServerConnectionParser;
import co.fusionx.relay.parser.main.ServerLineParser;
import co.fusionx.relay.sender.relay.RelayCapSender;
import co.fusionx.relay.sender.relay.RelayInternalSender;
import co.fusionx.relay.sender.relay.RelayPacketSender;
import co.fusionx.relay.util.SocketUtils;
import co.fusionx.relay.util.Utils;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;
import static co.fusionx.relay.parser.connection.ServerConnectionParser.ParseStatus;

public class RelayIRCConnection {

    private final ServerConfiguration mServerConfiguration;

    private final RelayPacketSender mRelayPacketSender;

    private final RelayServer mServer;

    private final RelayCapSender mCapSender;

    private final RelayInternalSender mInternalSender;

    private Thread mMainThread;

    private ConnectionStatus mStatus = ConnectionStatus.DISCONNECTED;

    private Socket mSocket;

    private int mReconnectAttempts;

    private boolean mStopped;

    RelayIRCConnection(final ServerConfiguration serverConfiguration) {
        mServerConfiguration = serverConfiguration;

        mServer = new RelayServer(serverConfiguration, this);
        mRelayPacketSender = mServer.getRelayPacketSender();
        mCapSender = new RelayCapSender(mRelayPacketSender);
        mInternalSender = new RelayInternalSender(mRelayPacketSender);
    }

    void startConnection() {
        mMainThread = new Thread(() -> {
            try {
                connectToServer();
            } catch (final Exception ex) {
                getPreferences().handleException(ex);
            }
        });
        mMainThread.start();
    }

    void stopConnection() {
        // Send the stop events and set the status before we talk to the server - ensures
        // that we don't get concurrent modifications
        onStopped();

        if (mStatus == ConnectionStatus.CONNECTED) {
            mStopped = true;
            mInternalSender.quitServer(getPreferences().getQuitReason());
        } else if (mMainThread.isAlive()) {
            mMainThread.interrupt();
        }
    }

    RelayServer getServer() {
        return mServer;
    }

    ConnectionStatus getStatus() {
        return mStatus;
    }

    /**
     * Method which keeps trying to reconnect to the server the number of times specified and if
     * the user has not explicitly tried to disconnect
     */
    private void connectToServer() {
        connect();

        for (mReconnectAttempts = 0; !mStopped && isReconnectNeeded(); mReconnectAttempts++) {
            onReconnecting();

            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                // This interrupt will *should* only ever occur if the user explicitly kills
                // reconnection
                return;
            }
            connect();
        }
        if (mStopped) {
            return;
        }
        onDisconnected("Disconnected from server (no reconnect pending).", false);
    }

    /**
     * Closes the socket if it is not already closed
     */
    private void closeSocket() {
        if (mSocket == null || mSocket.isClosed()) {
            mSocket = null;
            return;
        }

        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = null;
    }

    private void connect() {
        String disconnectMessage = "";
        try {
            mSocket = SocketUtils.openSocketConnection(mServerConfiguration);

            final BufferedWriter socketWriter = SocketUtils.getSocketBufferedWriter(mSocket);
            mServer.onOutputStreamCreated(socketWriter);

            // We are now in the phase where we can say we are connecting to the server
            onConnecting();

            // By sending this line, the server *should* wait until we end the CAP negotiation
            // That is if the server supports IRCv3
            mCapSender.sendLs();

            // Follow RFC2812's recommended order of sending - PASS -> NICK -> USER
            if (Utils.isNotEmpty(mServerConfiguration.getServerPassword())) {
                mInternalSender.sendServerPassword(mServerConfiguration.getServerPassword());
            }
            mServer.sendNick(mServerConfiguration.getNickStorage().getFirst());
            mInternalSender.sendUser(mServerConfiguration.getServerUserName(),
                    Utils.returnNonEmpty(mServerConfiguration.getRealName(), "RelayUser"));

            final BufferedReader reader = SocketUtils.getSocketBufferedReader(mSocket);
            final ServerConnectionParser parser = new ServerConnectionParser(mServer,
                    mServerConfiguration, reader, mRelayPacketSender);
            final ServerConnectionParser.ConnectionLineParseStatus status = parser.parseConnect();

            // This nick may well be different from any of the nicks in storage - get the
            // *official* nick from the server itself and use it
            // If the nick is null then we have no hope of progressing
            if (status.getStatus() == ParseStatus.NICK && Utils.isNotEmpty(status.getNick())) {
                onStartParsing(status.getNick(), reader);
            }
        } catch (final IOException ex) {
            // Usually occurs when WiFi/3G is turned off on the device - usually fruitless to try
            // to reconnect but hey ho
            disconnectMessage = ex.getMessage();
        }

        // If it was stopped then this cleanup would have already been performed
        if (mStopped) {
            onStopped();
        } else {
            onDisconnected(disconnectMessage, isReconnectNeeded());
        }
        closeSocket();
        mServer.onConnectionTerminated();
    }

    private void onStartParsing(final String nick, final BufferedReader reader) throws IOException {
        // Since we are now connected, reset the reconnect attempts
        mReconnectAttempts = 0;
        mServer.getUser().setNick(nick);
        onConnected();

        // Identifies with NickServ if the password exists
        if (Utils.isNotEmpty(mServerConfiguration.getNickservPassword())) {
            mInternalSender.sendNickServPassword(mServerConfiguration.getNickservPassword());
        }

        final Collection<RelayChannel> channels = mServer.getUser().getChannels();
        if (channels.isEmpty()) {
            // Automatically join the channels specified in the configuration
            for (final String channelName : mServerConfiguration.getAutoJoinChannels()) {
                mServer.sendJoin(channelName);
            }
        } else {
            for (final RelayChannel channel : channels) {
                mServer.sendJoin(channel.getName());
            }
        }

        // Initialise the parser used to parse any lines from the server
        final ServerLineParser lineParser = new ServerLineParser(mServer);
        // Loops forever until broken
        lineParser.parseMain(reader, mRelayPacketSender);
    }

    private void onConnecting() {
        mStatus = ConnectionStatus.CONNECTING;

        mServer.postAndStoreEvent(new ConnectingEvent(mServer));
    }

    private void onReconnecting() {
        mStatus = ConnectionStatus.RECONNECTING;

        mServer.postAndStoreEvent(new ReconnectEvent(mServer));
    }

    private void onConnected() {
        onStatusChanged(ConnectionStatus.CONNECTED,
                ChannelConnectEvent::new,
                QueryConnectEvent::new,
                server -> new ConnectEvent(server, mServerConfiguration.getUrl()));
    }

    private void onDisconnected(final String serverMessage, final boolean retryPending) {
        onStatusChanged(ConnectionStatus.DISCONNECTED,
                channel -> new ChannelDisconnectEvent(channel, serverMessage),
                user -> new QueryDisconnectEvent(user, serverMessage),
                server -> new DisconnectEvent(server, serverMessage, retryPending));
    }

    private void onStopped() {
        mStatus = ConnectionStatus.STOPPED;

        for (final RelayChannel channel : mServer.getUser().getChannels()) {
            channel.postAndStoreEvent(new ChannelStopEvent(channel));
            channel.markInvalid();
        }

        for (final RelayQueryUser user : mServer.getUserChannelInterface().getQueryUsers()) {
            user.postAndStoreEvent(new QueryStopEvent(user));
            user.markInvalid();
        }

        mServer.postAndStoreEvent(new StopEvent(mServer));
        mServer.markInvalid();
    }

    private void onStatusChanged(final ConnectionStatus status,
            final Function<RelayChannel, ChannelEvent> channelFunction,
            final Function<RelayQueryUser, QueryEvent> queryFunction,
            final Function<Server, ServerEvent> serverFunction) {
        mStatus = status;

        for (final RelayChannel channel : mServer.getUser().getChannels()) {
            channel.postAndStoreEvent(channelFunction.apply(channel));
        }

        for (final RelayQueryUser user : mServer.getUserChannelInterface().getQueryUsers()) {
            user.postAndStoreEvent(queryFunction.apply(user));
        }

        mServer.postAndStoreEvent(serverFunction.apply(mServer));
    }

    private boolean isReconnectNeeded() {
        return mReconnectAttempts < getPreferences().getReconnectAttemptsCount();
    }
}