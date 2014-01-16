package com.fusionx.relay.connection;

import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.interfaces.EventPreferences;
import com.fusionx.relay.misc.InterfaceHolders;

import android.os.Handler;
import android.util.Pair;

import java.util.Iterator;
import java.util.Map;

import gnu.trove.map.hash.THashMap;

public class ConnectionManager {

    private static ConnectionManager sConnectionManager;

    private final Map<String, ServerConnection> mConnectionMap = new THashMap<>();

    private ConnectionManager() {
    }

    /**
     * Returns a singleton connection manager which is lazily created
     *
     * @param preferences a concrete implementation of the {@link com.fusionx.relay.interfaces.EventPreferences}
     *                    interface
     * @return the connection manager which was created
     */
    public static ConnectionManager getConnectionManager(final EventPreferences preferences) {
        if (sConnectionManager == null) {
            sConnectionManager = new ConnectionManager();
            InterfaceHolders.onInterfaceReceived(preferences);
        }
        return sConnectionManager;
    }

    /**
     * Creates a connection with the IRC server and tries to connect to it
     *
     * @param configuration the configuration you want to connect with
     * @param errorHandler  a handler object which will be used if an error occurs on the background
     *                      thread
     * @return a pair of objects - the first item is a boolean which is true if the server already
     * exists in the manager. The second item is the server which was created.
     */
    public Pair<Boolean, Server> onConnectionRequested(final ServerConfiguration configuration,
            final Handler errorHandler) {
        final boolean existingServer = mConnectionMap.containsKey(configuration.getTitle());
        final ServerConnection connection;
        if (existingServer) {
            connection = mConnectionMap.get(configuration.getTitle());
        } else {
            connection = new ServerConnection(configuration, errorHandler);
            connection.start();
            mConnectionMap.put(configuration.getTitle(), connection);
        }
        return new Pair<>(existingServer, connection.getServer());
    }

    /**
     * Returns the number of servers which are currently managed by this manager
     *
     * @return the number of servers which are managed
     */
    public int getServerCount() {
        return mConnectionMap.size();
    }

    /**
     * Disconnect from the server with the specified name
     *
     * @param serverName the name of the server you're wanting to disconnect from
     * @return whether the list of connected servers is empty
     */
    public boolean onDisconnectionRequested(final String serverName) {
        final ServerConnection connection = mConnectionMap.get(serverName);
        if (connection != null) {
            connection.disconnect();
            mConnectionMap.remove(serverName);
        }
        return mConnectionMap.isEmpty();
    }

    /**
     * This method should be called when the server has notified the user that a disconnect has
     * occurred and there are no retries pending
     *
     * @param serverName the name of the server that this event came from
     */
    public boolean onFinalUnexpectedDisconnect(final String serverName) {
        mConnectionMap.remove(serverName);
        return mConnectionMap.isEmpty();
    }

    /**
     * Disconnects all the managed servers
     */
    public void disconnectAll() {
        final Iterator<ServerConnection> iterator = mConnectionMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().disconnect();
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
        if (mConnectionMap.containsKey(serverName)) {
            return mConnectionMap.get(serverName).getServer();
        } else {
            return null;
        }
    }
}