package com.fusionx.relay.call;

public class ChannelTopicCall extends Call {

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
