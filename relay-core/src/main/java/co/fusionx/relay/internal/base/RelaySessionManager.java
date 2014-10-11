package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.core.Session;
import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.core.SessionManager;
import co.fusionx.relay.core.SessionStatus;

public class RelaySessionManager implements SessionManager {

    private final Map<String, RelaySession> mSessionMap = new HashMap<>();

    private RelaySessionManager() {
    }

    /**
     * Returns a session manager to manage multiple IRC sessions
     *
     * @return the connection manager which was created
     */
    public static SessionManager createSessionManager() {
        return new RelaySessionManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Boolean, RelaySession> requestConnection(final SessionConfiguration configuration) {
        final ConnectionConfiguration connectionConfiguration = configuration
                .getConnectionConfiguration();
        RelaySession session = mSessionMap.get(connectionConfiguration.getTitle());

        final boolean exists = session != null;
        if (!exists) {
            session = new RelaySession(configuration);
            session.startSession();

            mSessionMap.put(connectionConfiguration.getTitle(), session);
        }
        return Pair.of(exists, session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestReconnection(final Session session) {
        final RelaySession relaySession = mSessionMap.get(session.getServer().getTitle());

        if (relaySession == null) {
            throw new IllegalArgumentException("Server not managed by this manager");
        }

        if (relaySession.getStatus() != SessionStatus.DISCONNECTED) {
            throw new IllegalArgumentException("Server not in disconnected state");
        }

        relaySession.startSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requestStoppageAndRemoval(final String serverName) {
        final RelaySession session = mSessionMap.get(serverName);
        if (session != null) {
            session.stopSession();
            mSessionMap.remove(serverName);
        }
        return mSessionMap.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDisconnectAll() {
        for (final RelaySession connection : mSessionMap.values()) {
            connection.stopSession();
        }
        mSessionMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<? extends Session> getSessionIfExists(final String sessionTitle) {
        final RelaySession connection = mSessionMap.get(sessionTitle);
        return Optional.fromNullable(connection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return mSessionMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImmutableSet<? extends Session> sessionSet() {
        return FluentIterable.from(mSessionMap.values()).toSet();
    }
}