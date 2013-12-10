/*
    HoloIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of HoloIRC.

    HoloIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HoloIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HoloIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.androidirclibrary.connection;

import com.fusionx.androidirclibrary.AppUser;
import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.ServerConfiguration;
import com.fusionx.androidirclibrary.communication.ServerSenderBus;
import com.fusionx.androidirclibrary.event.JoinEvent;
import com.fusionx.androidirclibrary.event.NickChangeEvent;
import com.fusionx.androidirclibrary.event.QuitEvent;
import com.fusionx.androidirclibrary.misc.InterfaceHolders;
import com.fusionx.androidirclibrary.parser.ServerConnectionParser;
import com.fusionx.androidirclibrary.parser.ServerLineParser;
import com.fusionx.androidirclibrary.util.SSLUtils;
import com.fusionx.androidirclibrary.writers.ServerWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

/**
 * Class which carries out all the interesting connection stuff including the initial setting up
 * logic
 *
 * @author Lalit Maganti
 */
class BaseConnection {

    private final Server server;

    private final ServerConfiguration serverConfiguration;

    private Socket mSocket;

    private boolean mUserDisconnected;

    private int reconnectAttempts;

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

        connect();

        while (isReconnectNeeded()) {
            server.getServerSenderBus().sendGenericServerEvent(server,
                    "Trying to reconnect to the server in 5 seconds.");
            try {
                Thread.sleep(5000);
            } catch (final InterruptedException e) {
                // This interrupt will *should* only ever occur if the user explicitly kills
                // reconnection
                return;
            }
            connect();
            ++reconnectAttempts;
        }

        server.getServerSenderBus().sendDisconnect(server, "Disconnected from the server",
                false);
    }

    /**
     * Called by the connectToServer method ONLY
     */
    private void connect() {
        final ServerSenderBus sender = server.getServerSenderBus();
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

            if (StringUtils.isNotEmpty(serverConfiguration.getServerPassword())) {
                serverWriter.sendServerPassword(serverConfiguration.getServerPassword());
            }

            serverWriter.changeNick(new NickChangeEvent("", serverConfiguration.getNickStorage()
                    .getFirstChoiceNick()));
            serverWriter.sendUser(serverConfiguration.getServerUserName(),
                    StringUtils.isNotEmpty(serverConfiguration.getRealName()) ?
                            serverConfiguration.getRealName() : "HoloIRC");

            final BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket
                    .getInputStream()));
            final String nick = ServerConnectionParser.parseConnect(server, serverConfiguration,
                    reader, serverWriter);

            onConnected(sender);

            // This nick may well be different from any of the nicks in storage - get the
            // *official* nick from the server itself and use it
            if (nick != null) {
                // Since we are now connected, reset the reconnect attempts
                reconnectAttempts = 0;

                final AppUser user = new AppUser(nick, server.getUserChannelInterface());
                server.setUser(user);

                // Identifies with NickServ if the password exists
                if (StringUtils.isNotEmpty(serverConfiguration.getNickservPassword())) {
                    serverWriter.sendNickServPassword(serverConfiguration
                            .getNickservPassword());
                }

                // Automatically join the channels specified in the configuration
                for (String channelName : serverConfiguration.getAutoJoinChannels()) {
                    server.getServerReceiverBus().post(new JoinEvent(channelName));
                }

                // Initialise the parser used to parse any lines from the server
                final ServerLineParser parser = new ServerLineParser(server, this);
                // Loops forever until broken
                parser.parseMain(reader, serverWriter);

                // If we have reached this point the connection has been broken - try to
                // reconnect unless the disconnection was requested by the user or we have used
                // all out lives
                if (isReconnectNeeded()) {
                    sender.sendDisconnect(server, "Disconnected from the server", true);
                }
            }
        } catch (final IOException ex) {
            // Usually occurs when WiFi/3G is turned off on the device - usually fruitless to try
            // to reconnect but hey ho
            if (isReconnectNeeded()) {
                sender.sendDisconnect(server, ex.getMessage(), true);
            }
        }
        if (!mUserDisconnected) {
            // We are disconnected :( - close up shop
            server.setStatus(InterfaceHolders.getEventResponses().getDisconnectedStatus());
            server.onCleanup();
            closeSocket();
        }
    }

    /**
     * Called to setup the socket
     */
    private void setupSocket() throws IOException {
        final SSLSocketFactory sslSocketFactory = SSLUtils.getCorrectSSLSocketFactory
                (serverConfiguration.shouldAcceptAllSSLCertificates());

        final InetSocketAddress address = new InetSocketAddress(serverConfiguration.getUrl(),
                serverConfiguration.getPort());

        mSocket = serverConfiguration.isSslEnabled() ? sslSocketFactory.createSocket() : new Socket();
        mSocket.setKeepAlive(true);
        mSocket.connect(address, 5000);
    }

    /**
     * Called when we are connected to the server
     */
    private void onConnected(final ServerSenderBus sender) {
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
        server.getServerSenderBus().post(new QuitEvent(InterfaceHolders.getPreferences()
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