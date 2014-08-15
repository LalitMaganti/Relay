package co.fusionx.relay.event.channel;

import co.fusionx.relay.Channel;

public class ChannelPartEvent extends ChannelEvent {

    public ChannelPartEvent(Channel channel) {
        super(channel.getName());
    }
}
