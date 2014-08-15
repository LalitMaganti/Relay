package co.fusionx.relay.parser.command;

import com.google.common.base.Optional;

import co.fusionx.relay.ChannelUser;
import co.fusionx.relay.RelayChannel;
import co.fusionx.relay.RelayChannelUser;
import co.fusionx.relay.RelayServer;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.function.Optionals;

import java.util.List;

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

        final ChannelWorldUserEvent event = getEvent(parsedArray, rawSource, channel, removedUser);
        mServerEventBus.postAndStoreEvent(event, channel);
    }
}