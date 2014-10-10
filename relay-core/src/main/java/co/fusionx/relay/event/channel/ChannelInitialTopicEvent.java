package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;

public class ChannelInitialTopicEvent extends ChannelEvent {

    public final String setterNick;

    public final String topic;

    public ChannelInitialTopicEvent(final Channel channel, final String setterNick,
            final String topic) {
        super(channel);
        this.setterNick = setterNick;
        this.topic = topic;
    }
}