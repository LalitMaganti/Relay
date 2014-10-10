package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.event.Event;

// Empty class
public class StatusChangeEvent extends Event<Server, ServerEvent> {

    public StatusChangeEvent(final Server server) {
        super(server);
    }
}