package co.fusionx.relay.base;

import com.google.common.base.Optional;

import android.util.Pair;

import java.util.Set;

public interface ConnectionManager {

    /**
     * Creates a connection with the IRC server and tries to connect to it
     *
     * @param configuration the configuration you want to connect with
     * @return a pair of objects - the first item is a boolean which is true if the server already
     * exists in the manager. The second item is the connection created.
     */
    Pair<Boolean, ? extends IRCSession> requestConnection(ServerConfiguration configuration);

    /**
     * Reconnect to the specified server
     *
     * @param server the connection to reconnect
     * @throws IllegalArgumentException if the server is not in this manager or if the server is
     *                                  not in the ConnectionStatus.Disconnected state
     */
    void requestReconnection(IRCSession server);

    /**
     * Disconnect from the server with the specified name and removes it from this manager
     *
     * This method should be called even when the server is in the DISCONNECTED state as the
     * server needs to be removed from this manager in this state
     *
     * @param serverName the name of the server you're wanting to disconnect from
     * @return whether the list of connected servers is empty
     */
    boolean requestStoppageAndRemoval(String serverName);

    /**
     * Disconnect and remove all the servers in the manager
     */
    void requestDisconnectAll();

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
    Optional<IRCSession> getConnectionIfExists(String serverName);

    /**
     * Returns the number of servers which are currently managed by this manager
     *
     * @return the number of servers which are managed
     */
    int getServerCount();

    /**
     * Returns an immutable set of the servers which are currently managed by this manager
     *
     * @return an immutable set of the servers which are managed by this manager
     */
    Set<? extends IRCSession> getConnectionSet();
}
