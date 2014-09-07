package co.fusionx.relay.internal.packet.server;

import co.fusionx.relay.internal.packet.Packet;

public class WhoisPacket implements Packet {

    public final String nick;

    public WhoisPacket(String nick) {
        this.nick = nick;
    }

    @Override
    public String getLine() {
        return String.format("WHOIS %s", nick);
    }
}