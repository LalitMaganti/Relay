package co.fusionx.relay.call.channel;

import co.fusionx.relay.call.Call;
import co.fusionx.relay.misc.WriterCommands;

public class ChannelMessageCall implements Call {

    private final String channelName;

    private final String message;

    public ChannelMessageCall(String channelName, String message) {
        this.channelName = channelName;
        this.message = message;
    }

    @Override
    public String getLineToSendServer() {
        return String.format(WriterCommands.PRIVMSG, channelName, message);
    }
}