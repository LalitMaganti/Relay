package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;

public class WhoisEvent extends ServerEvent {

    public final String whoisMessage;

    public WhoisEvent(final Server server, final String whoisMessage) {
        super(server);

        this.whoisMessage = whoisMessage;
    }
}