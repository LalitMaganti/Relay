package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;
import com.fusionx.relay.WorldUser;

public class TopicEvent extends ChannelEvent {

    public final String topicSetter;

    public final String topic;

    public TopicEvent(final Channel channel, final WorldUser user, final String newTopic) {
        super(channel);

        topicSetter = user.getPrettyNick(channel);
        topic = newTopic;
    }
}