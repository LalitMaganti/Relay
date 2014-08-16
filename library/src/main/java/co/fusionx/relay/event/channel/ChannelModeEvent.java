package co.fusionx.relay.event.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.Channel;
import co.fusionx.relay.ChannelUser;

public class ChannelModeEvent extends ChannelEvent {

    public final String recipient;

    public final Optional<? extends ChannelUser> sendingUser;

    public final String sendingNick;

    public final String mode;

    public ChannelModeEvent(final Channel channel,
            final Optional<? extends ChannelUser> sendingUser, final String sendingNick,
            final String recipient, final String mode) {
        super(channel);

        this.recipient = recipient;
        this.sendingUser = sendingUser;
        this.sendingNick = sendingNick;
        this.mode = mode;
    }
}