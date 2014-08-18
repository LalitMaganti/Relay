package co.fusionx.relay.call.channel;

import co.fusionx.relay.call.Call;

public class ChannelTopicCall implements Call {

    private final String mTopic;

    private final String mChannelName;

    public ChannelTopicCall(String channelName, String newTopic) {
        mChannelName = channelName;
        mTopic = newTopic;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("TOPIC %s %s", mChannelName, mTopic);
    }
}
