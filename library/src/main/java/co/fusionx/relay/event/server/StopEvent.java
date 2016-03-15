package co.fusionx.relay.event.server;

import co.fusionx.relay.base.Server;

public class StopEvent extends StatusChangeEvent {

    public StopEvent(final Server server) {
        super(server);
    }
}