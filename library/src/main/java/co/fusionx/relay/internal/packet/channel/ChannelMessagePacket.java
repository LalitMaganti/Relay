package co.fusionx.relay.internal.packet.channel;

import co.fusionx.relay.internal.packet.Packet;

public class ChannelMessagePacket implements Packet {

    private final String channelName;

    private final String message;

    public ChannelMessagePacket(String channelName, String message) {
        this.channelName = channelName;
        this.message = message;
    }

    @Override
    public String getLine() {
        return String.format("PRIVMSG %1$s :%2$s", channelName, message);
    }
}