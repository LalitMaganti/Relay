package com.fusionx.relay.connection;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.ServerStatus;
import com.fusionx.relay.call.ChannelJoinCall;
import com.fusionx.relay.call.NickChangeCall;
import com.fusionx.relay.call.QuitCall;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.event.server.ConnectEvent;
import com.fusionx.relay.event.server.DisconnectEvent;
import com.fusionx.relay.event.server.GenericServerEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.parser.ServerConnectionParser;
import com.fusionx.relay.parser.ServerLineParser;
import com.fusionx.relay.util.SSLUtils;
import com.fusionx.relay.util.Utils;
import com.fusionx.relay.writers.ServerWriter;

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

    private final Server server;

    private final ServerConfiguration serverConfiguration;

    private Socket mSocket;

    private boolean mUserDisconnected;

    private int reconnectAttempts;

    private Collection<String> channelList;

    /**
     * Constructor for the object - package local since this object should always be contained only
     * within a {@link ServerConnection} object
     *
     * @param configuration - the ServerConfiguration which should be used to connect to the server
     */
    BaseConnection(final ServerConfiguration configuration, final Server serverObject) {
        server = serverObject;
        serverConfiguration = configuration;
    }

    /**
     * Method which keeps trying to reconnect to the server the number of times specified and if the
     * user has not explicitly tried to disconnect
     */
    void connectToServer() {
        reconnectAttempts = 0;

        channelList = serverConfiguration.getAutoJoinChannels();

        connect();

        while (isReconnectNeeded()) {
            server.getServerEventBus().postAndStoreEvent(new GenericServerEvent("Trying to "
                    + "reconnect to the server in 5 seconds."), server);
            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                // This interrupt will *should* only ever occur if the user explicitly kills
                // reconnection
                return;
            }
            // Make sure the cleanup happens before the next connection but after all the tabs
            // have been closed
            server.onCleanup();

            connect();
            ++reconnectAttempts;
        }

        if (!mUserDisconnected) {
            server.getServerEventBus().postAndStoreEvent(new DisconnectEvent("Disconnected from the "
                    + "server", false, false), server);
        }
    }

    /**
     * Called by the connectToServer method ONLY
     */
    private void connect() {
        final ServerEventBus sender = server.getServerEventBus();
        try {
            setupSocket();
            final Writer writer = new BufferedWriter(new OutputStreamWriter(mSocket
                    .getOutputStream()));
            final ServerWriter serverWriter = server.onOutputStreamCreated(writer);

            server.setStatus(ServerStatus.CONNECTING);

            if (serverConfiguration.isSaslAvailable()) {
                // By sending this line, the server *should* wait until we end the CAP stuff with CAP
                // END
                serverWriter.getSupportedCapabilities();
            }

            if (Utils.isNotEmpty(serverConfiguration.getServerPassword())) {
                serverWriter.sendServerPassword(serverConfiguration.getServerPassword());
            }

            serverWriter.changeNick(new NickChangeCall(serverConfiguration.getNickStorage()
                    .getFirstChoiceNick()));
            serverWriter.sendUser(serverConfiguration.getServerUserName(),
                    Utils.isNotEmpty(serverConfiguration.getRealName()) ?
                            serverConfiguration.getRealName() : "HoloIRC");

            final BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket
                    .getInputStream()));
            final ServerConnectionParser parser = new ServerConnectionParser(server,
                    serverConfiguration, reader, serverWriter);
            final String nick = parser.parseConnect();

            onConnected(sender);

            // This nick may well be different from any of the nicks in storage - get the
            // *official* nick from the server itself and use it
            if (nick != null) {
                // Since we are now connected, reset the reconnect attempts
                reconnectAttempts = 0;

                final AppUser user = new AppUser(nick, server.getUserChannelInterface());
                server.setUser(user);

                // Identifies with NickServ if the password exists
                if (Utils.isNotEmpty(serverConfiguration.getNickservPassword())) {
                    serverWriter.sendNickServPassword(serverConfiguration
                            .getNickservPassword());
                }

                // Automatically join the channels specified in the configuration
                for (final String channelName : channelList) {
                    server.getServerCallBus().post(new ChannelJoinCall(channelName));
                }

                // Initialise the parser used to parse any lines from the server
                final ServerLineParser lineParser = new ServerLineParser(server, this);
                // Loops forever until broken
                lineParser.parseMain(reader, serverWriter);

                // If we have reached this point the connection has been broken - try to
                // reconnect unless the disconnection was requested by the user or we have used
                // all our lives
                if (isReconnectNeeded()) {
                    sender.postAndStoreEvent(new DisconnectEvent("Disconnected from the server",
                            false, true), server);

                    channelList = server.getUser().getChannelList();
                }
            }
        } catch (final IOException ex) {
            // Usually occurs when WiFi/3G is turned off on the device - usually fruitless to try
            // to reconnect but hey ho
            if (isReconnectNeeded()) {
                sender.postAndStoreEvent(new DisconnectEvent("Disconnected from the server (" +
                        ex.getMessage() + ")", false, true), server);

                if (server.getUser() != null) {
                    channelList = server.getUser().getChannelList();
                }
            }
        }
        if (!mUserDisconnected) {
            // We are disconnected :( - close up shop
            server.setStatus(ServerStatus.DISCONNECTED);
            closeSocket();
        }
    }

    /**
     * Called to setup the socket
     */
    private void setupSocket() throws IOException {
        final SSLSocketFactory sslSocketFactory = SSLUtils.getAppropriateSSLFactory
                (serverConfiguration.shouldAcceptAllSSLCertificates());

        final InetSocketAddress address = new InetSocketAddress(serverConfiguration.getUrl(),
                serverConfiguration.getPort());

        mSocket = serverConfiguration.isSslEnabled() ? sslSocketFactory.createSocket()
                : new Socket();
        mSocket.setKeepAlive(true);
        mSocket.connect(address, 5000);
    }

    /**
     * Called when we are connected to the server
     */
    private void onConnected(final ServerEventBus sender) {
        // We are connected
        server.setStatus(ServerStatus.CONNECTED);

        final ServerEvent event = new ConnectEvent(serverConfiguration.getUrl());
        sender.postAndStoreEvent(event, server);
    }

    /**
     * Called when the user explicitly requests a disconnect
     */
    public void onDisconnect() {
        mUserDisconnected = true;
        server.setStatus(ServerStatus.DISCONNECTED);
        server.getServerCallBus().post(new QuitCall(InterfaceHolders.getPreferences()
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
        return reconnectAttempts < InterfaceHolders.getPreferences().getReconnectAttemptsCount()
                && !mUserDisconnected;
    }

    public boolean isUserDisconnected() {
        return mUserDisconnected;
    }
}