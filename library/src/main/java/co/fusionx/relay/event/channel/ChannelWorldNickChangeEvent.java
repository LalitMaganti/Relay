package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.Nick;

public class ChannelWorldNickChangeEvent extends ChannelWorldUserEvent {

    public final Nick oldNick;

    public ChannelWorldNickChangeEvent(final Channel channel, final Nick oldNick,
            final ChannelUser user) {
        super(channel, user);

        this.oldNick = oldNick;
    }
}