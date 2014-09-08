package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;

public abstract class RemoveUserParser extends CommandParser {

    public RemoveUserParser(final Server server,
            final RelayUserChannelGroup ucmanager,
            final RelayQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);

        final Optional<RelayChannel> optChannel = mUCManager.getChannel(channelName);
        Optionals.run(optChannel, channel -> {
            final Optional<RelayChannelUser> optUser = getRemovedUser(parsedArray, prefix);
            Optionals.run(optUser, user -> {
                if (mUCManager.getUser().isNickEqual(user)) {
                    onRemoved(parsedArray, prefix, channel);
                } else {
                    onUserRemoved(parsedArray, prefix, channel, user);
                }
            }, () -> LogUtils.logOptionalBug(optUser, mServer));
        }, () -> LogUtils.logOptionalBug(optChannel, mServer));
    }

    abstract Optional<RelayChannelUser> getRemovedUser(final List<String> parsedArray,
            final String rawSource);

    abstract ChannelWorldUserEvent getEvent(final List<String> parsedArray,
            final String rawSource, final RelayChannel channel, final ChannelUser user);

    abstract void onRemoved(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel);

    private void onUserRemoved(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel, final RelayChannelUser removedUser) {
        mUCManager.decoupleUserAndChannel(removedUser, channel);

        channel.getBus().post(getEvent(parsedArray, rawSource, channel, removedUser));
    }
}