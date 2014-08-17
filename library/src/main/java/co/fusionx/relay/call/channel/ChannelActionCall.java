package co.fusionx.relay.call.channel;

import co.fusionx.relay.call.Call;
import co.fusionx.relay.misc.WriterCommands;

public class ChannelActionCall extends Call {

    private final String action;

    private final String channelName;

    public ChannelActionCall(final String channelName, final String action) {
        this.action = action;
        this.channelName = channelName;
    }

    @Override
    public String getLineToSendServer() {
        return String.format(WriterCommands.ACTION, channelName, action);
    }
}