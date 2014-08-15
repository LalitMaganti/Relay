package co.fusionx.relay.dcc;

import java.util.Collection;

import co.fusionx.relay.dcc.pending.DCCPendingConnection;

/**
 * Created by lalit on 15/08/14.
 */
public interface DCCManager {

    Collection<DCCPendingConnection> getPendingConnections();
}
