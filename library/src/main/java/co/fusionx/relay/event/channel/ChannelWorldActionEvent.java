package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;

public class ChannelWorldActionEvent extends ChannelWorldUserEvent {

    public final String action;

    public ChannelWorldActionEvent(final Channel channel, final String action,
            final ChannelUser sendingUser, final boolean mention) {
        super(channel, sendingUser, mention);

        this.action = action;
    }

    public ChannelWorldActionEvent(final Channel channel, final String action,
            final String sendingUser, final boolean mention) {
        super(channel, sendingUser, mention);

        this.action = action;
    }
}