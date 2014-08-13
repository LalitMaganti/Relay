package com.fusionx.relay.event.channel;

import com.fusionx.relay.RelayMainUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.Nick;

public class ChannelNickChangeEvent extends ChannelEvent {

    public final Nick oldNick;

    public final Nick newNick;

    public final RelayMainUser mRelayMainUser;

    public ChannelNickChangeEvent(final Channel channel, final Nick oldNick, final RelayMainUser user) {
        super(channel);

        this.oldNick = oldNick;
        this.newNick = user.getNick();
        this.mRelayMainUser = user;
    }
}