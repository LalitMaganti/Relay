package co.fusionx.relay.dcc;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.dcc.connection.DCCChatConnection;
import co.fusionx.relay.dcc.connection.DCCConnection;
import co.fusionx.relay.dcc.connection.DCCFileConnection;
import co.fusionx.relay.dcc.pending.DCCPendingChatConnection;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.dcc.pending.DCCPendingFileConnection;
import gnu.trove.set.hash.THashSet;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayDCCManager implements DCCManager {

    private final Set<DCCConnection> mConnections;

    private final Set<DCCPendingConnection> mPendingConnections;

    private final RelayServer mRelayServer;

    public RelayDCCManager(final RelayServer relayServer) {
        mRelayServer = relayServer;
        mConnections = new THashSet<>();
        mPendingConnections = new THashSet<>();
    }

    public void addPendingConnection(final DCCPendingConnection pendingConnection) {
        mPendingConnections.add(pendingConnection);
    }

    @Override
    public Collection<DCCConnection> getActiveConnections() {
        return ImmutableSet.copyOf(mConnections);
    }

    @Override
    public Collection<DCCPendingConnection> getPendingConnections() {
        return ImmutableSet.copyOf(mPendingConnections);
    }

    public void acceptDCCConnection(final DCCPendingFileConnection pendingConnection,
            final File file) {
        acceptDCCConnection(pendingConnection,
                () -> new DCCFileConnection(mRelayServer, pendingConnection, file));
    }

    public void acceptDCCConnection(final DCCPendingChatConnection pendingConnection) {
        acceptDCCConnection(pendingConnection, () -> new DCCChatConnection(mRelayServer,
                pendingConnection));
    }

    private void acceptDCCConnection(final DCCPendingConnection pendingConnection,
            final Supplier<DCCConnection> connectionSupplier) {
        if (!equals(pendingConnection.getManager())) {
            // TODO - Maybe send an event instead?
            getPreferences().logServerLine("DCC Connection not managed by this server");
            return;
        }

        final DCCConnection connection = connectionSupplier.get();
        connection.startConnection();

        mConnections.add(connection);
        mPendingConnections.remove(pendingConnection);
    }

    public void declineDCCConnection(final DCCPendingConnection connection) {
        mPendingConnections.remove(connection);
    }
}