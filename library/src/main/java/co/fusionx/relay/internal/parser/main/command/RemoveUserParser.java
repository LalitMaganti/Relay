package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayUserChannelDao;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;

public abstract class RemoveUserParser extends CommandParser {

    public RemoveUserParser(final Server server, final RelayUserChannelDao dao) {
        super(server, dao);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String channelName = parsedArray.get(0);

        final Optional<RelayChannel> optChannel = mDao.getChannel(channelName);
        Optionals.run(optChannel, channel -> {
            final Optional<RelayChannelUser> optUser = getRemovedUser(parsedArray, prefix);
            Optionals.run(optUser, user -> {
                if (mUser.isNickEqual(user.getNick().getNickAsString())) {
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
        mDao.decoupleUserAndChannel(removedUser, channel);

        channel.getBus().post(getEvent(parsedArray, rawSource, channel, removedUser));
    }
}