package co.fusionx.relay.packet.server.internal;

import co.fusionx.relay.packet.Packet;

public class PongPacket implements Packet {

    public final String mSource;

    public PongPacket(final String source) {
        mSource = source;
    }

    @Override
    public String getLine() {
        return String.format("PONG %s", mSource);
    }
}