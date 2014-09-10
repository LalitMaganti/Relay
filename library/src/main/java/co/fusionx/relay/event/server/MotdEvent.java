package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;

public final class MotdEvent extends ServerEvent {

    public final String motdLine;

    public MotdEvent(final Server server, String motdLine) {
        super(server);

        this.motdLine = motdLine;
    }
}