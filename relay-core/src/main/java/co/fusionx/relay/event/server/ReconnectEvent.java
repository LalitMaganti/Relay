package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;

public class ReconnectEvent extends ServerEvent {

    public ReconnectEvent(final Server server) {
        super(server);
    }
}