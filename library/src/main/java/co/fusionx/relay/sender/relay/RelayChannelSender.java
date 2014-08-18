package co.fusionx.relay.sender.relay;

import com.google.common.base.Optional;

import co.fusionx.relay.base.relay.RelayChannel;
import co.fusionx.relay.call.channel.ChannelActionCall;
import co.fusionx.relay.call.channel.ChannelKickCall;
import co.fusionx.relay.call.channel.ChannelMessageCall;
import co.fusionx.relay.call.channel.ChannelPartCall;
import co.fusionx.relay.call.channel.ChannelTopicCall;
import co.fusionx.relay.call.server.ModeCall;
import co.fusionx.relay.sender.ChannelSender;

public class RelayChannelSender implements ChannelSender {

    private final RelayChannel mChannel;

    private final RelayServerLineSender mRelayServerLineSender;

    public RelayChannelSender(final RelayChannel channel,
            final RelayServerLineSender relayServerLineSender) {
        mChannel = channel;
        mRelayServerLineSender = relayServerLineSender;
    }

    @Override
    public void sendAction(final String action) {
        mRelayServerLineSender.post(new ChannelActionCall(mChannel.getName(), action));
    }

    @Override
    public void sendKick(final String userNick, final Optional<String> reason) {
        mRelayServerLineSender.post(new ChannelKickCall(mChannel.getName(), userNick, reason));
    }

    @Override
    public void sendMessage(final String message) {
        mRelayServerLineSender.post(new ChannelMessageCall(mChannel.getName(), message));
    }

    @Override
    public void sendPart(final Optional<String> reason) {
        mRelayServerLineSender.post(new ChannelPartCall(mChannel.getName(), reason));
    }

    @Override
    public void sendTopic(final String newTopic) {
        mRelayServerLineSender.post(new ChannelTopicCall(mChannel.getName(), newTopic));
    }

    @Override
    public void sendUserMode(final String userNick, final String mode) {
        mRelayServerLineSender.post(new ModeCall(mChannel.getName(), userNick, mode));
    }
}