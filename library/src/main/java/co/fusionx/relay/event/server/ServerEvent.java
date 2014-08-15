package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;
import co.fusionx.relay.event.Event;

public class ServerEvent extends Event {

    public final Server server;

    public ServerEvent(final Server server) {
        this.server = server;
    }
}