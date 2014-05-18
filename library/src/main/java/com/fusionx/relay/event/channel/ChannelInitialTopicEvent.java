package com.fusionx.relay.event.channel;

import com.fusionx.relay.Channel;

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