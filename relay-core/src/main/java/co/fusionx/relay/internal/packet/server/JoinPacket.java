package co.fusionx.relay.internal.packet.server;

import co.fusionx.relay.internal.packet.Packet;

public class JoinPacket implements Packet {

    private final String channelName;

    public JoinPacket(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String getLine() {
        return String.format("JOIN %s", channelName);
    }
}