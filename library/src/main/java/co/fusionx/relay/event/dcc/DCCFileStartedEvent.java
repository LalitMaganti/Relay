package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCGetConnection;

public class DCCFileStartedEvent extends DCCEvent {

    public DCCFileStartedEvent(final DCCGetConnection dccConnection) {
        super(dccConnection);
    }
}
