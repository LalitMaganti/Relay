package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.core.Nick;

public class ChannelWorldNickChangeEvent extends ChannelWorldUserEvent {

    public final Nick oldNick;

    public ChannelWorldNickChangeEvent(final Channel channel, final Nick oldNick,
            final ChannelUser user) {
        super(channel, user);

        this.oldNick = oldNick;
    }
}