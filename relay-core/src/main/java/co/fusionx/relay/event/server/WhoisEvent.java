package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.event.Event;

public class WhoisEvent extends Event<Server, ServerEvent> {

    public final String whoisMessage;

    public WhoisEvent(final Server server, final String whoisMessage) {
        super(server);

        this.whoisMessage = whoisMessage;
    }
}