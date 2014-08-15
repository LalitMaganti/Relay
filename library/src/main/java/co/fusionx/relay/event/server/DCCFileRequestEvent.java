package co.fusionx.relay.event.server;

public class DCCFileRequestEvent {

    public final String fileName;

    public final String ipAddress;

    public final int port;

    public final long size;

    public DCCFileRequestEvent(final String fileName, final String ipAddress, final int port,
            final long size) {
        this.fileName = fileName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.size = size;
    }
}
