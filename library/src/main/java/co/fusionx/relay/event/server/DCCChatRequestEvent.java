package co.fusionx.relay.event.server;

public class DCCChatRequestEvent extends ServerEvent {

    public final String ipAddress;

    public final int port;

    public DCCChatRequestEvent(final String ipAddress, final int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }
}