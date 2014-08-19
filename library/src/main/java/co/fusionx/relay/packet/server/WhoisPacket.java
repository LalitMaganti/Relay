package co.fusionx.relay.packet.server;

import co.fusionx.relay.packet.Packet;

public class WhoisPacket implements Packet {

    public final String nick;

    public WhoisPacket(String nick) {
        this.nick = nick;
    }

    @Override
    public String getLineToSendServer() {
        return "WHOIS " + nick;
    }
}