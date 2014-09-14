package co.fusionx.relay.core;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import android.util.Pair;

public interface SessionManager {

    /**
     * Creates a connection with the IRC server and tries to connect to it
     *
     * @param configuration the configuration you want to use for the session
     * @return a pair of objects - the first item is a boolean which is true if the server already
     * exists in the manager. The second item is the connection created.
     */
    Pair<Boolean, Session> requestConnection(SessionConfiguration configuration);

    /**
     * Reconnect to the specified session
     *
     * @param session the connection to reconnect
     * @throws IllegalArgumentException if the session is not in this manager or if the session is
     *                                  not in the ConnectionStatus.Disconnected state
     */
    void requestReconnection(Session session);

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
     * @param sessionTitle the name of the server you're wanting to get
     * @return the server with the required title if it exists - this may be null
     */
    Optional<Session> getSessionIfExists(String sessionTitle);

    /**
     * Returns the number of servers which are currently managed by this manager
     *
     * @return the number of servers which are managed
     */
    public int size();

    /**
     * Returns an immutable copy of the servers which are currently managed by this manager
     *
     * @return an immutable copy of the servers which are managed by this manager
     */
    public ImmutableSet<? extends Session> sessionSet();
}
