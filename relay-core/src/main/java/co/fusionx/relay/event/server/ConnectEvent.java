package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;

public class ConnectEvent extends ServerEvent {

    public final String serverUrl;

    public ConnectEvent(final Server server, final String serverUrl) {
        super(server);

        this.serverUrl = serverUrl;
    }
}