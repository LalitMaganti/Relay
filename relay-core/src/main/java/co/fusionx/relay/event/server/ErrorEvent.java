package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.event.Event;

public class ErrorEvent extends Event<Server, ServerEvent> {

    public final String line;

    public ErrorEvent(final Server server, final String rawLine) {
        super(server);

        line = rawLine;
    }
}