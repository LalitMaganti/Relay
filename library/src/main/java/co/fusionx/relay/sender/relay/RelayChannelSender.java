package co.fusionx.relay.sender.relay;

import com.google.common.base.Optional;

import co.fusionx.relay.base.relay.RelayChannel;
import co.fusionx.relay.packet.channel.ChannelActionPacket;
import co.fusionx.relay.packet.channel.ChannelKickPacket;
import co.fusionx.relay.packet.channel.ChannelMessagePacket;
import co.fusionx.relay.packet.channel.ChannelPartPacket;
import co.fusionx.relay.packet.channel.ChannelTopicPacket;
import co.fusionx.relay.packet.server.ModePacket;
import co.fusionx.relay.sender.ChannelSender;

public class RelayChannelSender implements ChannelSender {

    private final RelayChannel mChannel;

    private final RelayPacketSender mRelayPacketSender;

    public RelayChannelSender(final RelayChannel channel,
            final RelayPacketSender relayPacketSender) {
        mChannel = channel;
        mRelayPacketSender = relayPacketSender;
    }

    @Override
    public void sendAction(final String action) {
        mRelayPacketSender.post(new ChannelActionPacket(mChannel.getName(), action));
    }

    @Override
    public void sendKick(final String userNick, final Optional<String> reason) {
        mRelayPacketSender.post(new ChannelKickPacket(mChannel.getName(), userNick, reason));
    }

    @Override
    public void sendMessage(final String message) {
        mRelayPacketSender.post(new ChannelMessagePacket(mChannel.getName(), message));
    }

    @Override
    public void sendPart(final Optional<String> reason) {
        mRelayPacketSender.post(new ChannelPartPacket(mChannel.getName(), reason));
    }

    @Override
    public void sendTopic(final String newTopic) {
        mRelayPacketSender.post(new ChannelTopicPacket(mChannel.getName(), newTopic));
    }

    @Override
    public void sendUserMode(final String userNick, final String mode) {
        mRelayPacketSender.post(new ModePacket(mChannel.getName(), userNick, mode));
    }
}