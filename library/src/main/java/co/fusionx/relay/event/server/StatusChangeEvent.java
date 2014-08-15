package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;

// Empty class
public class StatusChangeEvent extends ServerEvent {

    public StatusChangeEvent(final Server server) {
        super(server);
    }
}