package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.event.Event;

public class ServerEvent extends Event<Server, ServerEvent> {

    public ServerEvent(final Server server) {
        super(server);
    }
}