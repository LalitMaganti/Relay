package co.fusionx.relay.event.server;

import co.fusionx.relay.base.Server;

public class ConnectEvent extends StatusChangeEvent {

    public final String serverUrl;

    public ConnectEvent(final Server server, final String serverUrl) {
        super(server);

        this.serverUrl = serverUrl;
    }
}