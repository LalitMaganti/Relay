package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;

public abstract class RemoveUserParser extends CommandParser {

    RemoveUserParser(RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(channelName);

        LogUtils.logOptionalBug(optChannel, mServer);
        Optionals.ifPresent(optChannel, channel -> {
            final Optional<RelayChannelUser> optUser = getRemovedUser(parsedArray, prefix);
            LogUtils.logOptionalBug(optUser, mServer);

            Optionals.ifPresent(optUser, user -> {
                if (mServer.getUser().isNickEqual(user.getNick().getNickAsString())) {
                    onRemoved(parsedArray, prefix, channel);
                } else {
                    onUserRemoved(parsedArray, prefix, channel, user);
                }
            });
        });
    }

    abstract Optional<RelayChannelUser> getRemovedUser(final List<String> parsedArray,
            final String rawSource);

    abstract ChannelWorldUserEvent getEvent(final List<String> parsedArray,
            final String rawSource, final RelayChannel channel, final ChannelUser user);

    abstract void onRemoved(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel);

    private void onUserRemoved(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel, final RelayChannelUser removedUser) {
        mUserChannelInterface.decoupleUserAndChannel(removedUser, channel);

        channel.postAndStoreEvent(getEvent(parsedArray, rawSource, channel, removedUser));
    }
}