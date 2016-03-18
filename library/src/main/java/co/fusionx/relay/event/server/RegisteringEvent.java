package co.fusionx.relay.event.server;

import co.fusionx.relay.base.Server;

public class RegisteringEvent extends StatusChangeEvent {

    public RegisteringEvent(final Server server) {
        super(server);
    }
}