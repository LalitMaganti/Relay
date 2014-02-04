package com.fusionx.relay.connection;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelSnapshot;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.ServerStatus;
import com.fusionx.relay.call.ChannelJoinCall;
import com.fusionx.relay.call.NickChangeCall;
import com.fusionx.relay.call.QuitCall;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.event.channel.ChannelConnectEvent;
import com.fusionx.relay.event.channel.ChannelDisconnectEvent;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.server.ConnectEvent;
import com.fusionx.relay.event.server.DisconnectEvent;
import com.fusionx.relay.event.server.GenericServerEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.event.user.UserConnectEvent;
import com.fusionx.relay.event.user.UserDisconnectEvent;
import com.fusionx.relay.event.user.UserEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.parser.ServerConnectionParser;
import com.fusionx.relay.parser.ServerLineParser;
import com.fusionx.relay.util.SSLUtils;
import com.fusionx.relay.util.Utils;
import com.fusionx.relay.writers.ServerWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;

import javax.net.ssl.SSLSocketFactory;

/**
 * Class which carries out all the interesting connection stuff including the initial setting up
 * logic
 *
 * @author Lalit Maganti
 */
public class BaseConnection {

    private final Server mServer;

    private final ServerConfiguration mServerConfiguration;

    private Socket mSocket;

    private boolean mUserDisconnected;

    private int mReconnectAttempts;

    private ServerLineParser mLineParser;

    /**
     * Constructor for the object - package local since this object should always be contained only
     * within a {@link ServerConnection} object
     *
     * @param configuration - the ServerConfiguration which should be used to connect to the server
     */
    BaseConnection(final ServerConfiguration configuration, final Server server) {
        mServer = server;
        mServerConfiguration = configuration;
    }

    /**
     * Method which keeps trying to reconnect to the server the number of times specified and if the
     * user has not explicitly tried to disconnect
     */
    void connectToServer() {
        mReconnectAttempts = 0;

        connect();

        while (isReconnectNeeded()) {
            mServer.getServerEventBus().postAndStoreEvent(new GenericServerEvent("Trying to "
                    + "reconnect to the server in 5 seconds."));
            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                // This interrupt will *should* only ever occur if the user explicitly kills
                // reconnection
                return;
            }

            connect();
            ++mReconnectAttempts;
        }

        if (!mUserDisconnected) {
            sendDisconnectEvents("", false, false);
        }
    }

    /**
     * Called by the connectToServer method ONLY
     */
    private void connect() {
        String disconnectMessage = "";
        try {
            setupSocket();
            final Writer writer = new BufferedWriter(new OutputStreamWriter(mSocket
                    .getOutputStream()));
            final ServerWriter serverWriter = mServer.onOutputStreamCreated(writer);

            mServer.setStatus(ServerStatus.CONNECTING);

            if (mServerConfiguration.isSaslAvailable()) {
                // By sending this line, the server *should* wait until we end the CAP stuff with CAP
                // END
                serverWriter.getSupportedCapabilities();
            }

            if (Utils.isNotEmpty(mServerConfiguration.getServerPassword())) {
                serverWriter.sendServerPassword(mServerConfiguration.getServerPassword());
            }

            serverWriter.changeNick(new NickChangeCall(mServerConfiguration.getNickStorage()
                    .getFirstChoiceNick()));
            serverWriter.sendUser(mServerConfiguration.getServerUserName(),
                    Utils.isNotEmpty(mServerConfiguration.getRealName()) ?
                            mServerConfiguration.getRealName() : "RelayUser");

            final BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket
                    .getInputStream()));
            final ServerConnectionParser parser = new ServerConnectionParser(mServer,
                    mServerConfiguration, reader, serverWriter);
            final String nick = parser.parseConnect();

            // This nick may well be different from any of the nicks in storage - get the
            // *official* nick from the server itself and use it
            if (nick != null) {
                onStartParsing(nick, serverWriter, reader);
            }
        } catch (final IOException ex) {
            // Usually occurs when WiFi/3G is turned off on the device - usually fruitless to try
            // to reconnect but hey ho
            disconnectMessage = ex.getMessage();
        }

        // If we have reached this point the connection has been broken - try to
        // reconnect unless the disconnection was requested by the user or we have used
        // all our lives
        if (isReconnectNeeded()) {
            sendDisconnectEvents(disconnectMessage, false, true);
        }

        if (!mUserDisconnected) {
            // We are disconnected :( - close up shop
            mServer.setStatus(ServerStatus.DISCONNECTED);
            closeSocket();

            mServer.onDisconnect();
        }
    }

    private void onStartParsing(final String nick, final ServerWriter serverWriter,
                                final BufferedReader reader) throws IOException {
        // Since we are now connected, reset the reconnect attempts
        mReconnectAttempts = 0;

        if (mServer.getUser() == null) {
            final AppUser user = new AppUser(nick, mServer.getUserChannelInterface());
            mServer.setUser(user);
        } else {
            mServer.getUser().setNick(nick);
        }

        onConnected();

        // Identifies with NickServ if the password exists
        if (Utils.isNotEmpty(mServerConfiguration.getNickservPassword())) {
            serverWriter.sendNickServPassword(mServerConfiguration
                    .getNickservPassword());
        }

        final Collection<ChannelSnapshot> channels = mServer.getUser()
                .getChannelSnapshots();
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
        mLineParser = new ServerLineParser(mServer, this);
        // Loops forever until broken
        mLineParser.parseMain(reader, serverWriter);
    }

    /**
     * Called to setup the socket
     */
    private void setupSocket() throws IOException {
        final InetSocketAddress address = new InetSocketAddress(mServerConfiguration.getUrl(),
                mServerConfiguration.getPort());
        if (mServerConfiguration.isSslEnabled()) {
            final SSLSocketFactory sslSocketFactory = SSLUtils.getSSLSocketFactory
                    (mServerConfiguration.shouldAcceptAllSSLCertificates());
            mSocket = sslSocketFactory.createSocket();
        } else {
            mSocket = new Socket();
        }

        mSocket.setKeepAlive(true);
        mSocket.connect(address, 5000);
    }

    /**
     * Called when we are connected to the server
     */
    private void onConnected() {
        mServer.setStatus(ServerStatus.CONNECTED);

        final ServerEventBus bus = mServer.getServerEventBus();

        final ServerEvent event = new ConnectEvent(mServerConfiguration.getUrl());
        bus.postAndStoreEvent(event);

        for (final Channel channel : mServer.getUser().getChannels()) {
            final ChannelEvent channelEvent = new ChannelConnectEvent(channel);
            bus.postAndStoreEvent(channelEvent, channel);
        }

        for (final PrivateMessageUser user : mServer.getUserChannelInterface()
                .getPrivateMessageUsers()) {
            final UserEvent userEvent = new UserConnectEvent(user);
            bus.postAndStoreEvent(userEvent, user);
        }
    }

    private void sendDisconnectEvents(final String serverMessage, boolean userSent,
                                      boolean retryPending) {
        final StringBuilder builder = new StringBuilder("Disconnected from the server");
        if (StringUtils.isNotEmpty(serverMessage)) {
            builder.append(" (").append(serverMessage).append(")");
        }
        final String message = builder.toString();
        final DisconnectEvent event = new DisconnectEvent(message, userSent, retryPending);

        mServer.getServerEventBus().postAndStoreEvent(event);

        for (final Channel channel : mServer.getUser().getChannels()) {
            final ChannelEvent channelEvent = new ChannelDisconnectEvent(channel, message);
            mServer.getServerEventBus().postAndStoreEvent(channelEvent, channel);
        }

        for (final PrivateMessageUser user : mServer.getUserChannelInterface()
                .getPrivateMessageUsers()) {
            final UserEvent userEvent = new UserDisconnectEvent(user, message);
            mServer.getServerEventBus().postAndStoreEvent(userEvent, user);
        }
    }

    /**
     * Called when the user explicitly requests a disconnect
     */
    public void disconnect() {
        mUserDisconnected = true;
        mServer.setStatus(ServerStatus.DISCONNECTED);
        mServer.getServerCallBus().post(new QuitCall(InterfaceHolders.getPreferences()
                .getQuitReason()));
    }

    /**
     * Closes the socket if it is not already closed
     */
    public void closeSocket() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isReconnectNeeded() {
        return mReconnectAttempts < InterfaceHolders.getPreferences().getReconnectAttemptsCount()
                && !mUserDisconnected;
    }

    public boolean isUserDisconnected() {
        return mUserDisconnected;
    }

    public String getCurrentLine() {
        if (mLineParser != null) {
            return mLineParser.getCurrentLine();
        }
        return "";
    }
}