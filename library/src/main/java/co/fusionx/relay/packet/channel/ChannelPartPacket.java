package co.fusionx.relay.packet.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.packet.Packet;

public class ChannelPartPacket implements Packet {

    public final static String PART = "PART %1$s";

    public final static String PART_WITH_REASON = "PART %1$s :%2$s";

    private final String mChannelName;

    private final Optional<String> mOptReason;

    public ChannelPartPacket(final String channelName, final Optional<String> optReason) {
        mChannelName = channelName;
        mOptReason = optReason;
    }

    @Override
    public String getLine() {
        return mOptReason.transform(this::partWithReason).or(String.format(PART, mChannelName));
    }

    private String partWithReason(final String reason) {
        return String.format(PART_WITH_REASON, mChannelName, reason).trim();
    }
}