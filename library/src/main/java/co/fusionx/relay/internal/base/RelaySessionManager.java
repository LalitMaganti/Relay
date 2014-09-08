package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.base.IRCSession;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.base.SessionManager;
import co.fusionx.relay.base.SessionStatus;
import co.fusionx.relay.interfaces.RelayConfiguration;
import co.fusionx.relay.misc.RelayConfigurationProvider;

public class RelaySessionManager implements SessionManager {

    private final Map<String, RelaySession> mSessionMap = new HashMap<>();

    private RelaySessionManager() {
    }

    /**
     * Returns a singleton connection manager which is lazily created
     *
     * @param preferences a concrete implementation of the
     *                    {@link co.fusionx.relay.interfaces.RelayConfiguration} interface
     * @return the connection manager which was created
     */
    public static SessionManager createSessionManager(final RelayConfiguration preferences) {
        final RelaySessionManager sSessionManager = new RelaySessionManager();
        RelayConfigurationProvider.onInterfaceReceived(preferences);
        return sSessionManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Boolean, IRCSession> requestConnection(final ServerConfiguration configuration) {
        RelaySession session = mSessionMap.get(configuration.getTitle());

        final boolean exists = session != null;
        if (!exists) {
            session = new RelaySession(configuration);
            session.startSession();

            mSessionMap.put(configuration.getTitle(), session);
        }
        return new Pair<>(exists, session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestReconnection(final IRCSession session) {
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
    public Optional<IRCSession> getConnectionIfExists(final String serverName) {
        final RelaySession connection = mSessionMap.get(serverName);
        return Optional.fromNullable(connection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSessionCount() {
        return mSessionMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<? extends IRCSession> getSessionSet() {
        return FluentIterable.from(mSessionMap.values()).toSet();
    }
}