package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.event.Event;

public class QuitEvent extends Event<Server, ServerEvent> {

    public QuitEvent(final Server server) {
        super(server);
    }
}