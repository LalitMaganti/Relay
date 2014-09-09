package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.LibraryUser;

public class ChannelActionEvent extends ChannelEvent {

    public final String action;

    public final LibraryUser user;

    public ChannelActionEvent(final Channel channel, final String action,
            final LibraryUser user) {
        super(channel);

        this.action = action;
        this.user = user;
    }
}