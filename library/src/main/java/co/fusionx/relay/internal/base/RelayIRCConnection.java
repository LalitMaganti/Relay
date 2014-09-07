package co.fusionx.relay.internal.base;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

import javax.inject.Inject;

import co.fusionx.relay.base.ConnectionStatus;
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
import co.fusionx.relay.internal.parser.connection.ConnectionParser;
import co.fusionx.relay.internal.parser.main.ServerLineParser;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.internal.sender.RelayCapSender;
import co.fusionx.relay.internal.sender.RelayInternalSender;
import co.fusionx.relay.util.SocketUtils;
import co.fusionx.relay.util.Utils;

import static co.fusionx.relay.internal.parser.connection.ConnectionParser.ParseStatus;
import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayIRCConnection {

    private final ServerConfiguration mServerConfiguration;

    private final RelayServer mServer;

    private final BaseSender mBaseSender;

    private final RelayInternalSender mInternalSender;

    private final RelayCapSender mCapSender;

    private Thread mConnectionThread;

    private Socket mSocket;

    private int mReconnectAttempts;

    private boolean mStopped;

    @Inject
    RelayIRCConnection(final ServerConfiguration serverConfiguration,
            final RelayServer server, final BaseSender baseSender) {
        mServerConfiguration = serverConfiguration;
        mServer = server;
        mBaseSender = baseSender;

        mInternalSender = new RelayInternalSender(baseSender);
        mCapSender = new RelayCapSender(baseSender);
    }

    void startConnection() {
        mConnectionThread = new Thread(this::connectToServerSilently);
        mConnectionThread.start();
    }

    void stopConnection() {
        if (mServer.getStatus() == ConnectionStatus.CONNECTED) {
            mStopped = true;
            mInternalSender.quitServer(getPreferences().getQuitReason());
        } else if (mConnectionThread.isAlive()) {
            mConnectionThread.interrupt();
        }
    }

    private void connectToServerSilently() {
        try {
            connectToServer();
        } catch (final Exception ex) {
            getPreferences().handleException(ex);
        }
    }

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
            initializeConnection();
        } catch (final IOException ex) {
            // Usually occurs when WiFi/3G is turned off on the device - usually fruitless to try
            // to reconnect but hey ho
            disconnectMessage = ex.getMessage();
        }

        if (mStopped) {
            onStopped();
        } else {
            onDisconnected(disconnectMessage, isReconnectNeeded());
        }
        closeSocket();
        mServer.onConnectionTerminated();
    }

    private void initializeConnection() throws IOException {
        mSocket = SocketUtils.openSocketConnection(mServerConfiguration);

        final BufferedReader socketReader = SocketUtils.getSocketBufferedReader(mSocket);
        final BufferedWriter socketWriter = SocketUtils.getSocketBufferedWriter(mSocket);
        mServer.onOutputStreamCreated(socketWriter);

        // We are now in the phase where we can say we are connecting to the server
        onConnecting();

        // Send the registration messages to the server
        sendInitialMessages();

        // Setup the connection parser and start parsing
        final ConnectionParser parser = new ConnectionParser(mServer, mBaseSender);
        final ConnectionParser.ConnectionLineParseStatus status = parser.parseConnect(socketReader);

        // This nick may well be different from any of the nicks in storage - get the
        // *official* nick from the server itself and use it
        // If the nick is null then we have no hope of progressing
        if (status.getStatus() == ParseStatus.NICK && Utils.isNotEmpty(status.getNick())) {
            onStartParsing(status.getNick(), socketReader);
        }
    }

    private void sendInitialMessages() {
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
        final ServerLineParser lineParser = new ServerLineParser(mServer, mBaseSender);
        // Loops forever until broken
        lineParser.parseMain(reader);
    }

    private void onConnecting() {
        mServer.updateStatus(ConnectionStatus.CONNECTING);

        mServer.postAndStoreEvent(new ConnectingEvent(mServer));
    }

    private void onReconnecting() {
        mServer.updateStatus(ConnectionStatus.RECONNECTING);

        mServer.postAndStoreEvent(new ReconnectEvent(mServer));
    }

    private void onConnected() {
        onStatusChanged(ConnectionStatus.CONNECTED,
                ChannelConnectEvent::new,
                QueryConnectEvent::new,
                () -> new ConnectEvent(mServer, mServerConfiguration.getUrl()));
    }

    private void onDisconnected(final String serverMessage, final boolean retryPending) {
        onStatusChanged(ConnectionStatus.DISCONNECTED,
                channel -> new ChannelDisconnectEvent(channel, serverMessage),
                user -> new QueryDisconnectEvent(user, serverMessage),
                () -> new DisconnectEvent(mServer, serverMessage, retryPending));
    }

    private void onStopped() {
        mServer.updateStatus(ConnectionStatus.STOPPED);

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
            final Supplier<ServerEvent> serverFunction) {
        mServer.updateStatus(status);

        for (final RelayChannel channel : mServer.getUser().getChannels()) {
            channel.postAndStoreEvent(channelFunction.apply(channel));
        }

        for (final RelayQueryUser user : mServer.getUserChannelInterface().getQueryUsers()) {
            user.postAndStoreEvent(queryFunction.apply(user));
        }

        mServer.postAndStoreEvent(serverFunction.get());
    }

    private boolean isReconnectNeeded() {
        return mReconnectAttempts < getPreferences().getReconnectAttemptsCount();
    }

    // Getters
    RelayServer getServer() {
        return mServer;
    }
}