package com.fusionx.relay.connection;

import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.interfaces.EventPreferences;
import com.fusionx.relay.interfaces.EventResponses;
import com.fusionx.relay.misc.InterfaceHolders;

import android.os.Handler;

import java.util.HashMap;
import java.util.Iterator;

public class ConnectionManager {

    private static ConnectionManager sConnectionManager;

    private final HashMap<String, ServerConnection> mServerMap
            = new HashMap<String, ServerConnection>();

    private ConnectionManager() {
    }

    /**
     * Returns a singleton connection manager which is lazily created
     *
     * @param responses   a concrete implementation of the {@link com.fusionx.relay.interfaces.EventResponses}
     *                    interface
     * @param preferences a concrete implementation of the {@link com.fusionx.relay.interfaces.EventPreferences}
     *                    interface
     * @return the connection manager which was created
     */
    public static ConnectionManager getConnectionManager(final EventResponses responses,
            final EventPreferences preferences) {
        if (sConnectionManager == null) {
            sConnectionManager = new ConnectionManager();
            InterfaceHolders.onInterfaceReceived(preferences, responses);
        }
        return sConnectionManager;
    }

    /**
     * Creates a connection with the IRC server and tries to connect to it
     *
     * @param configuration the configuration you want to connect with
     * @param errorHandler  a handler object which will be used if an error occurs on the background
     *                      thread
     * @return the server object created by the connection
     */
    public Server onConnectionRequested(final ServerConfiguration configuration,
            final Handler errorHandler) {
        final ServerConnection connection;
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
     * @return the number of servers which are managed
     */
    public int getConnectedServerCount() {
        return mServerMap.size();
    }

    /**
     * Disconnect from the server with the specified name
     *
     * @param serverName the name of the server you're wanting to disconnect from
     * @return whether the list of connected servers is empty
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
     * @param serverName the name of the server you're wanting to get
     * @return the server with the required title if it exists - this may be null
     */
    public Server getServerIfExists(final String serverName) {
        if (mServerMap.containsKey(serverName)) {
            return mServerMap.get(serverName).getServer();
        } else {
            return null;
        }
    }
}