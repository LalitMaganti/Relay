package co.fusionx.relay.internal.packet.channel;

import co.fusionx.relay.internal.packet.Packet;

public class ChannelActionPacket implements Packet {

    private final String mChannelName;

    private final String mAction;

    public ChannelActionPacket(final String channelName, final String action) {
        mChannelName = channelName;
        mAction = action;
    }

    @Override
    public String getLine() {
        return String.format("PRIVMSG %1$s :\u0001ACTION %2$s\u0001", mChannelName, mAction);
    }
}