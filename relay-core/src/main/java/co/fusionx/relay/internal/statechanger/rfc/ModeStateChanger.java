package co.fusionx.relay.internal.statechanger.rfc;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import java.util.List;

import co.fusionx.relay.event.channel.ChannelModeEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.parser.rfc.ModeParser;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class ModeStateChanger implements ModeParser.ModeObserver {

    private final InternalServer mServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    public ModeStateChanger(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup) {
        mServer = server;
        mUserChannelGroup = userChannelGroup;
    }

    private static String changesToString(final List<ModeParser.ModeChange> modeChanges) {
        return FluentIterable.from(modeChanges).transform(m -> {
            final String arguments = FluentIterable.from(m.getModeParams()).join(Joiner.on(" "));
            return String.format("%s %s", m.getMode(), arguments);
        }).join(Joiner.on(" "));
    }

    @Override
    public void onChannelMode(final String prefix, final String channelName,
            final List<ModeParser.ModeChange> modeChanges) {
        // The recipient is a channel (i.e. the mode of a user in the channel is being changed
        // or possibly the mode of the channel itself)
        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(channelName);

        Optionals.run(optChannel,
                channel -> parseChannelMode(prefix, channel, modeChanges),
                () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }

    private void parseChannelMode(final String prefix, final InternalChannel channel,
            final List<ModeParser.ModeChange> modeChanges) {
        final String nick = ParseUtils.getNickFromPrefix(prefix);
        final Optional<InternalChannelUser> channelUserOptional = mUserChannelGroup.getUser(nick);

        final String modeString = ModeStateChanger.changesToString(modeChanges);

        final ChannelModeEvent event = new ChannelModeEvent(channel, channelUserOptional, nick,
                modeString);
    }

    @Override
    public void onUserMode(final String prefix, final String recipient,
            final List<ModeParser.ModeChange> modeChanges) {
        // TODO
    }
}