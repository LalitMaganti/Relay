package co.fusionx.relay.parser.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.relay.RelayChannel;
import co.fusionx.relay.base.relay.RelayChannelUser;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.util.LogUtils;

public abstract class RemoveUserParser extends CommandParser {

    RemoveUserParser(RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(2);
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(channelName);

        LogUtils.logOptionalBug(optChannel, mServer);
        Optionals.ifPresent(optChannel, channel -> {
            final Optional<RelayChannelUser> optUser = getRemovedUser(parsedArray, rawSource);
            LogUtils.logOptionalBug(optUser, mServer);

            Optionals.ifPresent(optUser, user -> {
                if (mServer.getUser().isNickEqual(user.getNick().getNickAsString())) {
                    onRemoved(parsedArray, rawSource, channel);
                } else {
                    onUserRemoved(parsedArray, rawSource, channel, user);
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