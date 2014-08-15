package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;

public class ErrorEvent extends ServerEvent {

    public final String line;

    public ErrorEvent(final Server server, final String rawLine) {
        super(server);

        line = rawLine;
    }
}