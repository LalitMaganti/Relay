package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.base.ConnectionManager;
import co.fusionx.relay.base.ConnectionStatus;
import co.fusionx.relay.base.IRCConnection;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.interfaces.RelayConfiguration;
import co.fusionx.relay.misc.RelayConfigurationProvider;
import dagger.ObjectGraph;

public class RelayConnectionManager implements ConnectionManager {

    private static ConnectionManager sConnectionManager;

    private final Map<String, RelayIRCConnection> mConnectionMap = new HashMap<>();

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
    public Pair<Boolean, ? extends IRCConnection> requestConnection(final ServerConfiguration
            configuration) {
        RelayIRCConnection connection = mConnectionMap.get(configuration.getTitle());

        final boolean exists = connection != null;
        if (!exists) {
            final ObjectGraph objectGraph = ObjectGraph.create(new RelayBaseModule(configuration));
            connection = objectGraph.get(RelayIRCConnection.class);

            connection.startConnection();
            mConnectionMap.put(configuration.getTitle(), connection);
        }
        return new Pair<>(exists, connection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestReconnection(final IRCConnection connection) {
        final RelayIRCConnection realConnection = mConnectionMap
                .get(connection.getServer().getTitle());

        if (realConnection == null) {
            throw new IllegalArgumentException("Server not managed by this manager");
        }

        if (realConnection.getStatus() != ConnectionStatus.DISCONNECTED) {
            throw new IllegalArgumentException("Server not in disconnected state");
        }

        realConnection.startConnection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requestStoppageAndRemoval(final String serverName) {
        final RelayIRCConnection connection = mConnectionMap.get(serverName);
        if (connection != null) {
            connection.stopConnection();
            mConnectionMap.remove(serverName);
        }
        return mConnectionMap.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestDisconnectAll() {
        for (final RelayIRCConnection connection : mConnectionMap.values()) {
            connection.stopConnection();
        }
        mConnectionMap.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<IRCConnection> getConnectionIfExists(final String serverName) {
        final RelayIRCConnection connection = mConnectionMap.get(serverName);
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
    public Set<? extends IRCConnection> getConnectionSet() {
        return FluentIterable.from(mConnectionMap.values()).toSet();
    }
}