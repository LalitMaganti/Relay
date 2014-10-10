package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;

public class ConnectingEvent extends ServerEvent {

    public ConnectingEvent(final Server server) {
        super(server);
    }
}