package co.fusionx.relay.event.dcc;

import co.fusionx.relay.dcc.connection.DCCConnection;
import co.fusionx.relay.event.Event;

public class DCCEvent extends Event {

    public final DCCConnection dccConnection;

    public DCCEvent(final DCCConnection dccConnection) {
        this.dccConnection = dccConnection;
    }
}