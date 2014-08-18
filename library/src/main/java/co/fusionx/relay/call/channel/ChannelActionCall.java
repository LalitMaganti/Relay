package co.fusionx.relay.call.channel;

import co.fusionx.relay.call.Call;
import co.fusionx.relay.misc.WriterCommands;

public class ChannelActionCall implements Call {

    private final String mChannelName;

    private final String mAction;

    public ChannelActionCall(final String channelName, final String action) {
        mChannelName = channelName;
        mAction = action;
    }

    @Override
    public String getLineToSendServer() {
        return String.format(WriterCommands.ACTION, mChannelName, mAction);
    }
}