package co.fusionx.relay.sender;

import com.google.common.base.Optional;

import co.fusionx.relay.base.relay.RelayChannel;
import co.fusionx.relay.bus.ServerCallHandler;
import co.fusionx.relay.call.channel.ChannelActionCall;
import co.fusionx.relay.call.channel.ChannelKickCall;
import co.fusionx.relay.call.channel.ChannelMessageCall;
import co.fusionx.relay.call.channel.ChannelPartCall;
import co.fusionx.relay.call.channel.ChannelTopicCall;
import co.fusionx.relay.call.server.ModeCall;

public class RelayChannelSender implements ChannelSender {

    private final RelayChannel mChannel;

    private final ServerCallHandler mCallHandler;

    public RelayChannelSender(final RelayChannel channel, final ServerCallHandler callHandler) {
        mChannel = channel;
        mCallHandler = callHandler;
    }

    @Override
    public void sendAction(final String action) {
        mCallHandler.post(new ChannelActionCall(mChannel.getName(), action));
    }

    @Override
    public void sendKick(final String userNick, final Optional<String> reason) {
        mCallHandler.post(new ChannelKickCall(mChannel.getName(), userNick, reason));
    }

    @Override
    public void sendMessage(final String message) {
        mCallHandler.post(new ChannelMessageCall(mChannel.getName(), message));
    }

    @Override
    public void sendPart(final Optional<String> reason) {
        mCallHandler.post(new ChannelPartCall(mChannel.getName(), reason));
    }

    @Override
    public void sendTopic(final String newTopic) {
        mCallHandler.post(new ChannelTopicCall(mChannel.getName(), newTopic));
    }

    @Override
    public void sendUserMode(final String userNick, final String mode) {
        mCallHandler.post(new ModeCall(mChannel.getName(), userNick, mode));
    }
}