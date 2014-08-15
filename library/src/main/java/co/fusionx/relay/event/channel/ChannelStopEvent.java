package co.fusionx.relay.event.channel;

import co.fusionx.relay.Channel;

public class ChannelStopEvent extends ChannelEvent {

    public ChannelStopEvent(final Channel channel) {
        super(channel);
    }
}
