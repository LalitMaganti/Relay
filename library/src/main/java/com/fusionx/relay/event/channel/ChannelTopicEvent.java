package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;

public class ChannelTopicEvent extends ChannelEvent {

    public final ChannelUser topicSetter;

    public final String topic;

    public ChannelTopicEvent(final Channel channel, final ChannelUser user, final String newTopic) {
        super(channel);

        topicSetter = user;
        topic = newTopic;
    }
}