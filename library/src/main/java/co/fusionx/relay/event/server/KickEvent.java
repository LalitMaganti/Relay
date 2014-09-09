package co.fusionx.relay.event.server;

import com.google.common.base.Optional;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.core.Nick;
import co.fusionx.relay.conversation.Server;

public class KickEvent extends ServerEvent {

    public final Channel channel;

    public final Nick kickingNick;

    public final String kickingNickString;

    public final String reason;

    public KickEvent(final Server server, final Channel channel,
            final Optional<? extends ChannelUser> optKickingUser, final String kickingNickString,
            final String reason) {
        super(server);

        this.channel = channel;
        this.kickingNick = optKickingUser.transform(ChannelUser::getNick).orNull();
        this.kickingNickString = kickingNickString;
        this.reason = reason;
    }
}