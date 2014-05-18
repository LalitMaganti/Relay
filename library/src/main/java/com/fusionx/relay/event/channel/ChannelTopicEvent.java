package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class ChannelTopicEvent extends ChannelEvent {

    public final WorldUser topicSetter;

    public final String topic;

    public ChannelTopicEvent(final Channel channel, final WorldUser user, final String newTopic) {
        super(channel);

        topicSetter = user;
        topic = newTopic;
    }
}