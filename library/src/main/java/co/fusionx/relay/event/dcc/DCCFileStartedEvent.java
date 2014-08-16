package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCFileConnection;

public class DCCFileStartedEvent extends DCCEvent {

    public DCCFileStartedEvent(final DCCFileConnection dccConnection) {
        super(dccConnection);
    }
}
