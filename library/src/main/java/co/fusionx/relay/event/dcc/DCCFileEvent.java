package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCConnection;
import co.fusionx.relay.dcc.connection.DCCFileConnection;

public class DCCFileEvent extends DCCEvent {

    public DCCFileEvent(final DCCConnection dccConnection) {
        super(dccConnection);
    }

    public DCCFileConnection getConnection() {
        return (DCCFileConnection) dccConnection;
    }
}