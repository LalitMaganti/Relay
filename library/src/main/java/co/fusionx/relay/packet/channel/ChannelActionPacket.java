package co.fusionx.relay.packet.channel;

import co.fusionx.relay.packet.Packet;
import co.fusionx.relay.misc.WriterCommands;

public class ChannelActionPacket implements Packet {

    private final String mChannelName;

    private final String mAction;

    public ChannelActionPacket(final String channelName, final String action) {
        mChannelName = channelName;
        mAction = action;
    }

    @Override
    public String getLine() {
        return String.format(WriterCommands.ACTION, mChannelName, mAction);
    }
}