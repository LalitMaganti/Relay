package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;

public class DCCChatRequestEvent extends ServerEvent {

    public final String ipAddress;

    public final int port;

    public DCCChatRequestEvent(final Server server, final String ipAddress, final int port) {
        super(server);

        this.ipAddress = ipAddress;
        this.port = port;
    }
}