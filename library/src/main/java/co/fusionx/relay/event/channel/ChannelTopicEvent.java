package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;

public class ChannelTopicEvent extends ChannelEvent {

    public final ChannelUser topicSetter;

    public final String topic;

    public ChannelTopicEvent(final Channel channel, final ChannelUser user, final String newTopic) {
        super(channel);

        topicSetter = user;
        topic = newTopic;
    }
}