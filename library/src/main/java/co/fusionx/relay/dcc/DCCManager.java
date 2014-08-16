package co.fusionx.relay.dcc;

import java.util.Collection;

import co.fusionx.relay.dcc.connection.DCCConnection;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public interface DCCManager {

    public Collection<DCCConnection> getActiveConnections();

    public Collection<DCCPendingConnection> getPendingConnections();
}
