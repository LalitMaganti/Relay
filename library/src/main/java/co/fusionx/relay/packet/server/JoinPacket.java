package co.fusionx.relay.packet.server;

import co.fusionx.relay.packet.Packet;

public class JoinPacket implements Packet {

    private final String channelName;

    public JoinPacket(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String getLineToSendServer() {
        return "JOIN " + channelName;
    }
}