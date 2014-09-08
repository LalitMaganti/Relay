package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.base.ConnectionManager;
import co.fusionx.relay.base.IRCSession;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.base.SessionStatus;
import co.fusionx.relay.interfaces.RelayConfiguration;
import co.fusionx.relay.misc.RelayConfigurationProvider;

public class RelayConnectionManager implements ConnectionManager {

    private static ConnectionManager sConnectionManager;

    private final Map<String, RelaySession> mConnectionMap = new HashMap<>();

    private RelayConnectionManager() {
    }

    /**
     * Returns a singleton connection manager which is lazily created
     *
     * @param preferences a concrete implementation of the
     *                    {@link co.fusionx.relay.interfaces.RelayConfiguration} interface
     * @return the connection manager which was created
     */
    public static ConnectionManager getConnectionManager(final RelayConfiguration preferences) {
        if (sConnectionManager == null) {
            sConnectionManager = new RelayConnectionManager();
            RelayConfigurationProvider.onInterfaceReceived(preferences);
        }
        return sConnectionManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Boolean, ? extends IRCSession> requestConnection(final ServerConfiguration
            configuration) {
        RelaySession connection = mConnectionMap.get(configuration.getTitle());

        final boolean exists = connection != null;
        if (!exists) {
            connection = new RelaySession(configuration);
            connection.startSession();

            mConnectionMap.put(configuration.getTitle(), connection);
        }
        return new Pair<>(exists, connection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestReconnection(final IRCSession connection) {
        final RelaySession realConnection = mConnectionMap.get(connection.getServer().getTitle());

        if (realConnection == null) {
            throw new IllegalArgumentException("Server not managed by this manager");
        }

        if (realConnection.getStatus() != SessionStatus.DISCONNECTED) {
            throw new IllegalArgumentException("Server not in disconnected state");
        }

        realConnection.startSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requestStoppageAndRemoval(final String serverName) {
        final RelaySession connection = mConnectionMap.get(serverName);
        if (connection != null) {
            connection.stopSession();
            mConnectionMap.remove(serverName);
        }
        return mConnectionMap.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDisconnectAll() {
        for (final RelaySession connection : mConnectionMap.values()) {
            connection.stopSession();
        }
        mConnectionMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<IRCSession> getConnectionIfExists(final String serverName) {
        final RelaySession connection = mConnectionMap.get(serverName);
        return Optional.fromNullable(connection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getServerCount() {
        return mConnectionMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<? extends IRCSession> getConnectionSet() {
        return FluentIterable.from(mConnectionMap.values()).toSet();
    }
}