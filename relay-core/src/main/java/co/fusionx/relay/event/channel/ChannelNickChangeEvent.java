package co.fusionx.relay.event.channel;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.Nick;
import co.fusionx.relay.internal.core.InternalLibraryUser;

public class ChannelNickChangeEvent extends ChannelEvent {

    public final Nick oldNick;

    public final Nick newNick;

    public final InternalLibraryUser relayUser;

    public ChannelNickChangeEvent(final Channel channel, final Nick oldNick,
            final InternalLibraryUser user) {
        super(channel);

        this.oldNick = oldNick;
        this.newNick = user.getNick();
        this.relayUser = user;
    }
}