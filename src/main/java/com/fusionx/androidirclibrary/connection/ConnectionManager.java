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

    private HashMap<String, ServerConnection> mServerMap = new HashMap<String, ServerConnection>();

    private static ConnectionManager sConnectionManager;

    private ConnectionManager() {
    }

    public static ConnectionManager getConnectionManager(EventStringResponses responses,
            EventPreferences preferences) {
        if (sConnectionManager == null) {
            sConnectionManager = new ConnectionManager();
            InterfaceHolders.onInterfaceReceived(preferences, responses);
        }
        return sConnectionManager;
    }

    public void disconnectAll() {
        final Iterator<ServerConnection> iterator = mServerMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().disconnectFromServer();
            iterator.remove();
        }
    }

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

    public int getConnectedServerCount() {
        return mServerMap.size();
    }

    public boolean onDisconnectionRequested(final String serverName) {
        if (mServerMap.containsKey(serverName)) {
            mServerMap.remove(serverName);
        }
        return mServerMap.isEmpty();
    }
}