package co.fusionx.relay.internal.packet.server.ctcp.response;

public class VersionResponsePacket extends CTCPResponsePacket {

    public VersionResponsePacket(final String askingUser) {
        super(askingUser);
    }

    @Override
    public String getResponse() {
        return String.format("VERSION :%s", "Relay:1.0");
    }
}