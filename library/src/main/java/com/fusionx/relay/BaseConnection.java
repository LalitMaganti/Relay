package com.fusionx.relay;

import com.fusionx.relay.call.ChannelJoinCall;
import com.fusionx.relay.call.NickChangeCall;
import com.fusionx.relay.call.QuitCall;
import com.fusionx.relay.call.UserCall;
import com.fusionx.relay.event.channel.ChannelConnectEvent;
import com.fusionx.relay.event.channel.ChannelDisconnectEvent;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.ChannelStopEvent;
import com.fusionx.relay.event.query.QueryConnectEvent;
import com.fusionx.relay.event.query.QueryDisconnectEvent;
import com.fusionx.relay.event.query.QueryEvent;
import com.fusionx.relay.event.query.QueryStopEvent;
import com.fusionx.relay.event.server.ConnectEvent;
import com.fusionx.relay.event.server.ConnectingEvent;
import com.fusionx.relay.event.server.DisconnectEvent;
import com.fusionx.relay.event.server.ReconnectEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.event.server.StopEvent;
import com.fusionx.relay.parser.ServerConnectionParser;
import com.fusionx.relay.parser.ServerLineParser;
import com.fusionx.relay.util.SocketUtils;
import com.fusionx.relay.util.Utils;
import com.fusionx.relay.writers.ServerWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.net.Socket;
import java.util.Collection;

import static com.fusionx.relay.misc.InterfaceHolders.getPreferences;

/**
 * Class which carries out all the interesting connection stuff including the initial setting up
 * logic
 *
 * @author Lalit Maganti
 */
class BaseConnection {

    private final ServerConnection mServerConnection;

    private final Server mServer;

    private final ServerConfiguration mServerConfiguration;

    private Socket mSocket;

    private int mReconnectAttempts;

    private ServerLineParser mLineParser;

    private boolean mStopped;

    /**
     * Constructor for the object - package local since this object should always be contained only
     * within a {@link ServerConnection} object
     *
     * @param configuration the {@link ServerConfiguration} which should be used to connect to the
     *                      server
     * @param connection    the {@link ServerConnection} that created this object
     */
    BaseConnection(final ServerConfiguration configuration, final ServerConnection connection) {
        mServerConnection = connection;
        mServer = connection.getServer();
        mServerConfiguration = configuration;
    }

    String getCurrentLine() {
        if (mLineParser != null) {
            return mLineParser.getCurrentLine();
        }
        return "";
    }

    /**
     * Method which keeps trying to reconnect to the server the number of times specified and if
     * the user has not explicitly tried to disconnect
     */
    void connectToServer() {
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
        onDisconnected("Disconnected from server (no reconnect pending).", false);
    }

    /**
     * Called when the we reconnect to the server
     */
    private void onReconnecting() {
        // Set status to reconnecting
        mServerConnection.updateStatus(ConnectionStatus.RECONNECTING);

        mServer.getServerEventBus().postAndStoreEvent(new ReconnectEvent());
    }

    /**
     * Called when the user explicitly requests a disconnect
     */
    void stopConnection() {
        mStopped = true;
        mServer.getServerCallBus().postImmediately(new QuitCall(getPreferences().getQuitReason()));
    }

    /**
     * Closes the socket if it is not already closed
     */
    void closeSocket() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        String disconnectMessage = "";
        try {
            mSocket = SocketUtils.openSocketConnection(mServerConfiguration);

            final Writer writer = SocketUtils.getSocketWriter(mSocket);
            final ServerWriter serverWriter = mServer.onOutputStreamCreated(writer);

            onConnecting();

            if (mServerConfiguration.isSaslAvailable()) {
                // By sending this line, the server *should* wait until we end the CAP stuff with
                // CAP END
                serverWriter.sendSupportedCAP();
            }

            if (Utils.isNotEmpty(mServerConfiguration.getServerPassword())) {
                serverWriter.sendServerPassword(mServerConfiguration.getServerPassword());
            }

            serverWriter.sendNick(new NickChangeCall(mServerConfiguration.getNickStorage()
                    .getFirstChoiceNick()));
            mServer.getServerCallBus().post(new UserCall(mServerConfiguration.getServerUserName(),
                    Utils.isNotEmpty(mServerConfiguration.getRealName())
                            ? mServerConfiguration.getRealName()
                            : "RelayUser"
            ));

            final BufferedReader reader = SocketUtils.getSocketBufferedReader(mSocket);
            final ServerConnectionParser parser = new ServerConnectionParser(mServer,
                    mServerConfiguration, reader, serverWriter);
            final String nick = parser.parseConnect();

            // This nick may well be different from any of the nicks in storage - get the
            // *official* nick from the server itself and use it
            // If the nick is null then we have no hope of progressing
            if (nick != null) {
                onStartParsing(nick, serverWriter, reader);
            }
        } catch (final IOException ex) {
            // Usually occurs when WiFi/3G is turned off on the device - usually fruitless to try
            // to reconnect but hey ho
            disconnectMessage = ex.getMessage();
        }

        // If it was stopped then this cleanup would have already been performed
        if (!mStopped) {
            onDisconnected(disconnectMessage, isReconnectNeeded());
            closeSocket();
            mServer.onConnectionTerminated();
        }
    }

    private void onStartParsing(final String nick, final ServerWriter serverWriter,
            final BufferedReader reader) throws IOException {
        // Since we are now connected, reset the reconnect attempts
        mReconnectAttempts = 0;

        mServer.getUser().setNick(nick);

        onConnected();

        // Identifies with NickServ if the password exists
        if (Utils.isNotEmpty(mServerConfiguration.getNickservPassword())) {
            serverWriter.sendNickServPassword(mServerConfiguration
                    .getNickservPassword());
        }

        final Collection<Channel> channels = mServer.getUser().getChannels();
        if (channels.isEmpty()) {
            // Automatically join the channels specified in the configuration
            for (final String channelName : mServerConfiguration.getAutoJoinChannels()) {
                mServer.getServerCallBus().post(new ChannelJoinCall(channelName));
            }
        } else {
            for (final Channel channel : channels) {
                mServer.getServerCallBus().post(new ChannelJoinCall(channel.getName()));
            }
        }

        // Initialise the parser used to parse any lines from the server
        mLineParser = new ServerLineParser(mServer);
        // Loops forever until broken
        mLineParser.parseMain(reader, serverWriter);
    }

    private void onConnecting() {
        mServerConnection.updateStatus(ConnectionStatus.CONNECTING);

        mServer.getServerEventBus().postAndStoreEvent(new ConnectingEvent());
    }

    private void onConnected() {
        mServerConnection.updateStatus(ConnectionStatus.CONNECTED);

        for (final Channel channel : mServer.getUser().getChannels()) {
            final ChannelEvent channelEvent = new ChannelConnectEvent(channel);
            mServer.getServerEventBus().postAndStoreEvent(channelEvent, channel);
        }

        for (final QueryUser user : mServer.getUserChannelInterface().getQueryUsers()) {
            final QueryEvent queryEvent = new QueryConnectEvent(user);
            mServer.getServerEventBus().postAndStoreEvent(queryEvent, user);
        }

        final ServerEvent event = new ConnectEvent(mServerConfiguration.getUrl());
        mServer.getServerEventBus().postAndStoreEvent(event);
    }

    private void onDisconnected(final String serverMessage, final boolean retryPending) {
        mServerConnection.updateStatus(ConnectionStatus.DISCONNECTED);

        // User can be null if the server was not fully connected to
        if (mServer.getUser() != null) {
            for (final Channel channel : mServer.getUser().getChannels()) {
                final ChannelEvent channelEvent = new ChannelDisconnectEvent(channel,
                        serverMessage);
                mServer.getServerEventBus().postAndStoreEvent(channelEvent, channel);
            }
        }

        for (final QueryUser user : mServer.getUserChannelInterface().getQueryUsers()) {
            final QueryEvent queryEvent = new QueryDisconnectEvent(user, serverMessage);
            mServer.getServerEventBus().postAndStoreEvent(queryEvent, user);
        }

        final ServerEvent event = new DisconnectEvent(serverMessage, retryPending);
        mServer.getServerEventBus().postAndStoreEvent(event);
    }

    void onStopped() {
        mServerConnection.updateStatus(ConnectionStatus.STOPPED);

        // User can be null if the server was not fully connected to
        if (mServer.getUser() != null) {
            for (final Channel channel : mServer.getUser().getChannels()) {
                final ChannelEvent channelEvent = new ChannelStopEvent(channel);
                mServer.getServerEventBus().postAndStoreEvent(channelEvent, channel);
            }
        }

        for (final QueryUser user : mServer.getUserChannelInterface().getQueryUsers()) {
            final QueryEvent queryEvent = new QueryStopEvent(user);
            mServer.getServerEventBus().postAndStoreEvent(queryEvent, user);
        }

        final ServerEvent event = new StopEvent();
        mServer.getServerEventBus().postAndStoreEvent(event);
    }

    private boolean isReconnectNeeded() {
        return mReconnectAttempts < getPreferences().getReconnectAttemptsCount();
    }
}