package co.fusionx.relay.event.server;

import co.fusionx.relay.base.Server;

public class ReconnectEvent extends StatusChangeEvent {

    public ReconnectEvent(final Server server) {
        super(server);
    }
}