package com.fusionx.relay.connection;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.event.JoinEvent;
import com.fusionx.relay.event.NickChangeEvent;
import com.fusionx.relay.event.QuitEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.parser.ServerConnectionParser;
import com.fusionx.relay.parser.ServerLineParser;
import com.fusionx.relay.util.SSLUtils;
import com.fusionx.relay.util.Utils;
import com.fusionx.relay.writers.ServerWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
            server.getServerEventBus().sendGenericServerEvent(server,
                    "Trying to reconnect to the server in 5 seconds.");
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

        server.getServerEventBus().onDisconnected(server, "Disconnected from the server", false);
    }

    /**
     * Called by the connectToServer method ONLY
     */
    private void connect() {
        final ServerEventBus sender = server.getServerEventBus();
        try {
            setupSocket();
            final OutputStreamWriter writer = new OutputStreamWriter(mSocket.getOutputStream());
            final ServerWriter serverWriter = server.onOutputStreamCreated(writer);

            server.setStatus(InterfaceHolders.getEventResponses().getConnectingStatus());

            if (serverConfiguration.isSaslAvailable()) {
                // By sending this line, the server *should* wait until we end the CAP stuff with CAP
                // END
                serverWriter.getSupportedCapabilities();
            }

            if (Utils.isNotEmpty(serverConfiguration.getServerPassword())) {
                serverWriter.sendServerPassword(serverConfiguration.getServerPassword());
            }

            serverWriter.changeNick(new NickChangeEvent("", serverConfiguration.getNickStorage()
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
                for (String channelName : channelList) {
                    server.getServerCallBus().post(new JoinEvent(channelName));
                }

                // Initialise the parser used to parse any lines from the server
                final ServerLineParser lineParser = new ServerLineParser(server, this);
                // Loops forever until broken
                lineParser.parseMain(reader, serverWriter);

                // If we have reached this point the connection has been broken - try to
                // reconnect unless the disconnection was requested by the user or we have used
                // all our lives
                if (isReconnectNeeded()) {
                    sender.onDisconnected(server, "Disconnected from the server", true);

                    channelList = server.getUser().getChannelList();
                }
            }
        } catch (final IOException ex) {
            // Usually occurs when WiFi/3G is turned off on the device - usually fruitless to try
            // to reconnect but hey ho
            if (isReconnectNeeded()) {
                sender.onDisconnected(server, "Disconnected from the server (" + ex.getMessage()
                        + ")", true);

                if (server.getUser() != null) {
                    channelList = server.getUser().getChannelList();
                }
            }
        }
        if (!mUserDisconnected) {
            // We are disconnected :( - close up shop
            server.setStatus(InterfaceHolders.getEventResponses().getDisconnectedStatus());
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
        server.setStatus(InterfaceHolders.getEventResponses().getConnectedStatus());
        sender.sendConnected(server, serverConfiguration.getUrl());
    }

    /**
     * Called when the user explicitly requests a disconnect
     */
    public void onDisconnect() {
        mUserDisconnected = true;
        server.setStatus(InterfaceHolders.getEventResponses().getDisconnectedStatus());
        server.getServerCallBus().post(new QuitEvent(InterfaceHolders.getPreferences()
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