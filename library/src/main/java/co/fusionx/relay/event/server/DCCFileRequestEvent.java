package co.fusionx.relay.event.server;

import co.fusionx.relay.Server;

public class DCCFileRequestEvent extends ServerEvent {

    public final String fileName;

    public final String ipAddress;

    public final int port;

    public final long size;

    public DCCFileRequestEvent(final Server server, final String fileName, final String ipAddress,
            final int port, final long size) {
        super(server);

        this.fileName = fileName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.size = size;
    }
}
