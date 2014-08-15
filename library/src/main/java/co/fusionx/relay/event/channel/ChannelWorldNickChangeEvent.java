package co.fusionx.relay.event.channel;

import co.fusionx.relay.Channel;
import co.fusionx.relay.ChannelUser;
import co.fusionx.relay.Nick;

public class ChannelWorldNickChangeEvent extends ChannelWorldUserEvent {

    public final Nick oldNick;

    public ChannelWorldNickChangeEvent(final Channel channel, final Nick oldNick,
            final ChannelUser user) {
        super(channel, user);

        this.oldNick = oldNick;
    }
}