package co.fusionx.relay.internal.packet.channel;

import co.fusionx.relay.internal.packet.Packet;

public class ChannelTopicPacket implements Packet {

    public static final String TOPIC = "TOPIC %s %s";

    private final String mChannelName;

    private final String mTopic;

    public ChannelTopicPacket(String channelName, String newTopic) {
        mChannelName = channelName;
        mTopic = newTopic;
    }

    @Override
    public String getLine() {
        return String.format(TOPIC, mChannelName, mTopic);
    }
}
