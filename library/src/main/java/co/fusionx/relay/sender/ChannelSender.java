package co.fusionx.relay.sender;

import com.google.common.base.Optional;

public interface ChannelSender {

    public void sendAction(final String action);

    public void sendKick(final String userNick, final Optional<String> reason);

    public void sendMessage(final String message);

    public void sendPart(final Optional<String> reason);

    public void sendTopic(final String newTopic);

    public void sendUserMode(final String userNick, final String mode);
}