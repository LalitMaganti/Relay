package co.fusionx.relay.packet.server;

import co.fusionx.relay.packet.Packet;

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