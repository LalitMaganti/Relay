package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;

public class ImportantServerEvent extends ServerEvent {

    public final String message;

    ImportantServerEvent(final Server server, final String message) {
        super(server);

        this.message = message;
    }
}