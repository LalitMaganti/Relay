package co.fusionx.relay.internal.parser;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.core.Nick;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelNickChangeEvent;
import co.fusionx.relay.event.channel.ChannelWorldNickChangeEvent;
import co.fusionx.relay.event.server.ServerNickChangeEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class NickParser extends CommandParser {

    public NickParser(final InternalServer server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String oldRawNick = ParseUtils.getNickFromPrefix(prefix);
        final boolean appUser = mUserChannelGroup.getUser().isNickEqual(oldRawNick);
        final Optional<InternalChannelUser> optUser = appUser
                ? Optional.of(mUserChannelGroup.getUser())
                : mUserChannelGroup.getUser(oldRawNick);

        // The can happen in cases where gave a nick to the server but it ignored this nick and
        // gave use another one instead. Then half way through the server notice phase it
        // randomly decides to change our nick from the one we provided to the one which we have
        // already been given and using - simply ignore this bad nick change - Miau is a BNC
        // which displays this behaviour
        Optionals.run(optUser, user -> {
            final String newNick = parsedArray.get(0);
            final Nick oldNick = user.getNick();
            user.setNick(newNick);

            if (appUser) {
                mServer.postEvent(new ServerNickChangeEvent(mServer, oldNick, user));
            }

            for (final InternalChannel channel : user.getChannels()) {
                final ChannelEvent event = appUser
                        ? new ChannelNickChangeEvent(channel, oldNick, mUserChannelGroup.getUser())
                        : new ChannelWorldNickChangeEvent(channel, oldNick, user);
                channel.postEvent(event);
            }
        }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }
}