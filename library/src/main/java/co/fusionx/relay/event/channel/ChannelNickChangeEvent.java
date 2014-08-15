package co.fusionx.relay.event.channel;

import co.fusionx.relay.RelayMainUser;
import co.fusionx.relay.Channel;
import co.fusionx.relay.Nick;

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