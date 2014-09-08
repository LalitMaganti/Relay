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

    private final String mChannelName;

    private final BaseSender mRelayBaseSender;

    public RelayChannelSender(final String channelName, final BaseSender relayBaseSender) {
        mChannelName = channelName;
        mRelayBaseSender = relayBaseSender;
    }

    @Override
    public void sendAction(final String action) {
        mRelayBaseSender.sendPacket(new ChannelActionPacket(mChannelName, action));
    }

    @Override
    public void sendKick(final String userNick, final Optional<String> reason) {
        mRelayBaseSender.sendPacket(new ChannelKickPacket(mChannelName, userNick, reason));
    }

    @Override
    public void sendMessage(final String message) {
        mRelayBaseSender.sendPacket(new ChannelMessagePacket(mChannelName, message));
    }

    @Override
    public void sendPart(final Optional<String> reason) {
        mRelayBaseSender.sendPacket(new ChannelPartPacket(mChannelName, reason));
    }

    @Override
    public void sendTopic(final String newTopic) {
        mRelayBaseSender.sendPacket(new ChannelTopicPacket(mChannelName, newTopic));
    }

    @Override
    public void sendUserMode(final String userNick, final String mode) {
        mRelayBaseSender.sendPacket(new ModePacket(mChannelName, userNick, mode));
    }
}