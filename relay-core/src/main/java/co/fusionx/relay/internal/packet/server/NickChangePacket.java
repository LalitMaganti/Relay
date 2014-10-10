package co.fusionx.relay.internal.packet.server;

import co.fusionx.relay.internal.packet.Packet;

public class NickChangePacket implements Packet {

    private final String newNick;

    public NickChangePacket(String newNick) {
        this.newNick = newNick;
    }

    @Override
    public String getLine() {
        return String.format("NICK %s", newNick);
    }
}