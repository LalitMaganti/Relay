package com.fusionx.relay.connection;

import com.fusionx.relay.ConnectionStatus;
import com.fusionx.relay.Server;
import com.fusionx.relay.ServerConfiguration;
import com.fusionx.relay.interfaces.EventPreferences;
import com.fusionx.relay.misc.InterfaceHolders;

import android.os.Handler;
import android.util.Pair;

import java.util.List;
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
     * @param ignoreList    list of users who should be ignored - this can be changed in the
     *                      future using the updateIgnoreList method on the returned Server object
     * @param errorHandler  a handler object which will be used if an error occurs on the
     *                      background thread
     * @return a pair of objects - the first item is a boolean which is true if the server already
     * exists in the manager. The second item is the server which was created.
     */
    public Pair<Boolean, Server> requestConnection(final ServerConfiguration configuration,
            final List<String> ignoreList, final Handler errorHandler) {
        ServerConnection connection = mConnectionMap.get(configuration.getTitle());

        final boolean exists = connection != null;
        if (!exists) {
            connection = new ServerConnection(configuration, errorHandler, ignoreList);
            connection.connect();
            mConnectionMap.put(configuration.getTitle(), connection);
        }
        return new Pair<>(exists, connection.getServer());
    }

    /**
     *
     */
    public void requestReconnection(final Server server) {
        final ServerConnection connection = mConnectionMap.get(server.getTitle());

        if (connection == null) {
            throw new IllegalArgumentException("Server not managed by this manager");
        }

        if (connection.getStatus() != ConnectionStatus.DISCONNECTED) {
            throw new IllegalArgumentException("Server not in disconnected state");
        }

        connection.connect();
    }

    /**
     * Disconnect from the server with the specified name
     *
     * @param serverName the name of the server you're wanting to disconnect from
     * @return whether the list of connected servers is empty
     */
    public boolean requestDisconnection(final String serverName) {
        final ServerConnection connection = mConnectionMap.get(serverName);
        if (connection != null) {
            connection.disconnect();
            mConnectionMap.remove(serverName);
        }
        return mConnectionMap.isEmpty();
    }

    /**
     * Returns the server if it is already connected
     *
     * This is almost always NOT the method you want to call.
     * In most cases the {@code requestConnection} method should be called instead
     *
     * Only use this if you know what you're doing
     *
     * @param serverName the name of the server you're wanting to get
     * @return the server with the required title if it exists - this may be null
     */
    public Server getServerIfExists(final String serverName) {
        if (mConnectionMap.containsKey(serverName)) {
            return mConnectionMap.get(serverName).getServer();
        }
        return null;
    }

    /**
     * Returns the number of servers which are currently managed by this manager
     *
     * @return the number of servers which are managed
     */
    public int getServerCount() {
        return mConnectionMap.size();
    }
}