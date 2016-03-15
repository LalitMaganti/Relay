package co.fusionx.relay.event.server;

import com.google.common.base.Optional;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.Nick;
import co.fusionx.relay.internal.base.RelayChannelUser;

public class KickEvent extends ServerEvent {

    public final Channel channel;

    public final Nick kickingNick;

    public final String kickingNickString;

    public final String reason;

    public KickEvent(final Channel channel, final Optional<RelayChannelUser> optKickingUser,
            final String kickingNickString, final String reason) {
        super(channel.getServer());

        this.channel = channel;
        this.kickingNick = optKickingUser.transform(ChannelUser::getNick).orNull();
        this.kickingNickString = kickingNickString;
        this.reason = reason;
    }
}