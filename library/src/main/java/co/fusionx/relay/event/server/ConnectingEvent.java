package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;

public class ConnectingEvent extends StatusChangeEvent {

    public ConnectingEvent(final Server server) {
        super(server);
    }
}