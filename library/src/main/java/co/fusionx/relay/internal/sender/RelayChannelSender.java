package co.fusionx.relay.internal.sender;

import com.google.common.base.Optional;

import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.packet.channel.ChannelActionPacket;
import co.fusionx.relay.internal.packet.channel.ChannelKickPacket;
import co.fusionx.relay.internal.packet.channel.ChannelMessagePacket;
import co.fusionx.relay.internal.packet.channel.ChannelPartPacket;
import co.fusionx.relay.internal.packet.channel.ChannelTopicPacket;
import co.fusionx.relay.internal.packet.server.ModePacket;
import co.fusionx.relay.sender.ChannelSender;

public class RelayChannelSender implements ChannelSender {

    private final RelayChannel mChannel;

    private final BaseSender mRelayBaseSender;

    public RelayChannelSender(final RelayChannel channel,
            final BaseSender relayBaseSender) {
        mChannel = channel;
        mRelayBaseSender = relayBaseSender;
    }

    @Override
    public void sendAction(final String action) {
        mRelayBaseSender.sendPacket(new ChannelActionPacket(mChannel.getName(), action));
    }

    @Override
    public void sendKick(final String userNick, final Optional<String> reason) {
        mRelayBaseSender.sendPacket(new ChannelKickPacket(mChannel.getName(), userNick, reason));
    }

    @Override
    public void sendMessage(final String message) {
        mRelayBaseSender.sendPacket(new ChannelMessagePacket(mChannel.getName(), message));
    }

    @Override
    public void sendPart(final Optional<String> reason) {
        mRelayBaseSender.sendPacket(new ChannelPartPacket(mChannel.getName(), reason));
    }

    @Override
    public void sendTopic(final String newTopic) {
        mRelayBaseSender.sendPacket(new ChannelTopicPacket(mChannel.getName(), newTopic));
    }

    @Override
    public void sendUserMode(final String userNick, final String mode) {
        mRelayBaseSender.sendPacket(new ModePacket(mChannel.getName(), userNick, mode));
    }
}