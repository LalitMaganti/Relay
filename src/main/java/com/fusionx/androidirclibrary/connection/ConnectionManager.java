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

import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.ServerConfiguration;
import com.fusionx.androidirclibrary.interfaces.EventPreferences;
import com.fusionx.androidirclibrary.interfaces.EventStringResponses;
import com.fusionx.androidirclibrary.misc.InterfaceHolders;

import android.os.Handler;

import java.util.HashMap;
import java.util.Iterator;

public class ConnectionManager {

    private final HashMap<String, ServerConnection> mServerMap = new HashMap<String, ServerConnection>();

    private static ConnectionManager sConnectionManager;

    private ConnectionManager() {
    }

    /**
     * Returns a singleton connection manager which is lazily created
     *
     * @param responses   - a concrete implementation of the {@link com.fusionx.androidirclibrary
     *                    .interfaces.EventStringResponses interface}
     * @param preferences - a concrete implementation of the {@link com.fusionx.androidirclibrary
     *                    .interfaces.EventPreferences interface}
     * @return - the connection manager which was created
     */
    public static ConnectionManager getConnectionManager(EventStringResponses responses,
            EventPreferences preferences) {
        if (sConnectionManager == null) {
            sConnectionManager = new ConnectionManager();
            InterfaceHolders.onInterfaceReceived(preferences, responses);
        }
        return sConnectionManager;
    }

    /**
     * Creates a connection with the IRC server and tries to connect to it
     *
     * @param configuration - the configuration you want to connect with
     * @param errorHandler  - a handler object which will be used if an error occurs on the
     *                      background thread
     * @return - the server object created by the connection
     */
    public Server onConnectionRequested(final ServerConfiguration configuration,
            final Handler errorHandler) {
        ServerConnection connection;
        if (mServerMap.containsKey(configuration.getTitle())) {
            connection = mServerMap.get(configuration.getTitle());
        } else {
            connection = new ServerConnection(configuration, errorHandler);
            connection.start();
            mServerMap.put(configuration.getTitle(), connection);
        }
        return connection.getServer();
    }

    /**
     * Returns the number of servers which are currently managed by this manager
     *
     * @return - the number of servers which are managed
     */
    public int getConnectedServerCount() {
        return mServerMap.size();
    }

    /**
     * Disconnect from the server with the specified name
     *
     * @param serverName - the name of the server you're wanting to disconnect from
     * @return - whether the list of connected servers is empty
     */
    public boolean onDisconnectionRequested(final String serverName) {
        if (mServerMap.containsKey(serverName)) {
            mServerMap.remove(serverName);
        }
        return mServerMap.isEmpty();
    }

    /**
     * Disconnects all the managed servers
     */
    public void onDisconnectAll() {
        final Iterator<ServerConnection> iterator = mServerMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().onDisconnect();
            iterator.remove();
        }
    }

    /**
     * Returns the server if it is already connected to
     *
     * This is almost always NOT the method you want to call the {@code onConnectionRequested}
     * method instead
     *
     * Only use this if you know what you're doing
     *
     * @param serverName - the name of the server you're wanting to get
     * @return - the server with the required title if it exists - this may be null
     */
    public Server getServerIfExists(final String serverName) {
        if (mServerMap.containsKey(serverName)) {
            return mServerMap.get(serverName).getServer();
        } else {
            return null;
        }
    }
}