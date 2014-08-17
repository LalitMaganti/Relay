package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.event.Event;

public abstract class ChannelEvent extends Event {

    public final Channel channel;

    public final String channelName;

    ChannelEvent(final Channel channel) {
        this.channel = channel;
        this.channelName = channel.getName();
    }

    ChannelEvent(final String channelName) {
        this.channel = null;
        this.channelName = channelName;
    }
}