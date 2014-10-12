package co.fusionx.relay.event.server;

import com.google.common.base.Optional;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.core.ChannelUser;

public class KickEvent extends ServerEvent {

    public final Channel channel;

    public final Optional<? extends ChannelUser> optKickingUser;

    public final String kickingNickString;

    public final Optional<String> reason;

    public KickEvent(final Server server, final Channel channel,
            final Optional<? extends ChannelUser> optKickingUser, final String kickingNickString,
            final Optional<String> reason) {
        super(server);

        this.channel = channel;
        this.optKickingUser = optKickingUser;
        this.kickingNickString = kickingNickString;
        this.reason = reason;
    }
}