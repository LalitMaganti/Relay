package co.fusionx.relay.call.channel;

import android.text.TextUtils;

import co.fusionx.relay.call.Call;
import co.fusionx.relay.writers.WriterCommands;

public class ChannelPartCall extends Call {

    private final String channelName;

    private final String reason;

    public ChannelPartCall(final String channelName, final String reason) {
        this.channelName = channelName;
        this.reason = reason;
    }

    @Override
    public String getLineToSendServer() {
        return TextUtils.isEmpty(channelName) ? String.format(WriterCommands.PART, channelName)
                : String.format(WriterCommands.PART_WITH_REASON, channelName, reason).trim();
    }
}