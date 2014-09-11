package co.fusionx.relay.internal.parser;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.core.ChannelUser;
import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;

public abstract class RemoveUserParser extends CommandParser {

    public RemoveUserParser(final InternalServer server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);

        final Optional<InternalChannel> optChannel = mUserChannelGroup.getChannel(channelName);
        Optionals.run(optChannel, channel -> {
            final Optional<InternalChannelUser> optUser = getRemovedUser(parsedArray, prefix);
            Optionals.run(optUser, user -> {
                if (mUserChannelGroup.getUser().isNickEqual(user)) {
                    onRemoved(parsedArray, prefix, channel);
                } else {
                    onUserRemoved(parsedArray, prefix, channel, user);
                }
            }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
        }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }

    abstract Optional<InternalChannelUser> getRemovedUser(final List<String> parsedArray,
            final String rawSource);

    abstract ChannelWorldUserEvent getEvent(final List<String> parsedArray,
            final String rawSource, final InternalChannel channel, final ChannelUser user);

    abstract void onRemoved(final List<String> parsedArray, final String rawSource,
            final InternalChannel channel);

    private void onUserRemoved(final List<String> parsedArray, final String rawSource,
            final InternalChannel channel, final InternalChannelUser removedUser) {
        mUserChannelGroup.decoupleUserAndChannel(removedUser, channel);

        channel.postEvent(getEvent(parsedArray, rawSource, channel, removedUser));
    }
}