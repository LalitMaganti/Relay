package co.fusionx.relay.internal.packet.channel;

import co.fusionx.relay.internal.packet.Packet;

public class ChannelTopicPacket implements Packet {

    private final String mTopic;

    private final String mChannelName;

    public ChannelTopicPacket(String channelName, String newTopic) {
        mChannelName = channelName;
        mTopic = newTopic;
    }

    @Override
    public String getLine() {
        return String.format("TOPIC %s %s", mChannelName, mTopic);
    }
}
