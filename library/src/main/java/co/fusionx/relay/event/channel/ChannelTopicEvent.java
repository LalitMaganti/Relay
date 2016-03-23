package co.fusionx.relay.event.channel;

import java.util.List;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.FormatSpanInfo;

public class ChannelTopicEvent extends ChannelEvent {

    public final ChannelUser topicSetter;

    public final String topic;
    public final List<FormatSpanInfo> formats;

    public ChannelTopicEvent(final Channel channel, final ChannelUser user,
            final String newTopic, final List<FormatSpanInfo> topicFormats) {
        super(channel);

        topicSetter = user;
        topic = newTopic;
        formats = topicFormats;
    }
}