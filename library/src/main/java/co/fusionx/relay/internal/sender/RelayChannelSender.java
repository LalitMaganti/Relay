package co.fusionx.relay.internal.sender;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.SettingsProvider;
import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.event.channel.ChannelActionEvent;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelMessageEvent;
import co.fusionx.relay.internal.packet.channel.ChannelActionPacket;
import co.fusionx.relay.internal.packet.channel.ChannelKickPacket;
import co.fusionx.relay.internal.packet.channel.ChannelMessagePacket;
import co.fusionx.relay.internal.packet.channel.ChannelPartPacket;
import co.fusionx.relay.internal.packet.channel.ChannelTopicPacket;
import co.fusionx.relay.internal.packet.server.ModePacket;
import co.fusionx.relay.sender.ChannelSender;

public class RelayChannelSender implements ChannelSender {

    private final SettingsProvider mSettingsProvider;

    private final PacketSender mPacketSender;

    private final LibraryUser mUser;

    private Channel mChannel;

    public RelayChannelSender(final SettingsProvider settingsProvider,
            final PacketSender packetSender, final LibraryUser libraryUser) {
        mSettingsProvider = settingsProvider;
        mPacketSender = packetSender;
        mUser = libraryUser;
    }

    // I tried very hard to avoid this but no matter how much I tried to abstract this away,
    // circular dependencies keep popping up due to having to send the channel in events
    public void setChannel(final Channel channel) {
        mChannel = channel;
    }

    @Override
    public void sendAction(final String action) {
        mPacketSender.sendPacket(new ChannelActionPacket(mChannel.getName(), action));
        sendChannelSelfMessage(() -> new ChannelActionEvent(mChannel, action, mUser));
    }

    @Override
    public void sendKick(final String userNick, final Optional<String> reason) {
        mPacketSender.sendPacket(new ChannelKickPacket(mChannel.getName(), userNick, reason));
    }

    @Override
    public void sendMessage(final String message) {
        mPacketSender.sendPacket(new ChannelMessagePacket(mChannel.getName(), message));
        sendChannelSelfMessage(() -> new ChannelMessageEvent(mChannel, message, mUser));
    }

    @Override
    public void sendPart(final Optional<String> reason) {
        mPacketSender.sendPacket(new ChannelPartPacket(mChannel.getName(), reason));
    }

    @Override
    public void sendTopic(final String newTopic) {
        mPacketSender.sendPacket(new ChannelTopicPacket(mChannel.getName(), newTopic));
    }

    @Override
    public void sendUserMode(final String userNick, final String mode) {
        mPacketSender.sendPacket(new ModePacket(mChannel.getName(), userNick, mode));
    }

    private void sendChannelSelfMessage(final Supplier<ChannelEvent> function) {
        if (mSettingsProvider.isSelfEventHidden()) {
            return;
        }
        mChannel.getBus().post(function.get());
    }
}