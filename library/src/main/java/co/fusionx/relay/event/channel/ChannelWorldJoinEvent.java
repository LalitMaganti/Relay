package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;

public class ChannelWorldJoinEvent extends ChannelWorldUserEvent {

    public ChannelWorldJoinEvent(final Channel channel, final ChannelUser user) {
        super(channel, user);
    }
}