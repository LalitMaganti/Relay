package co.fusionx.relay.internal.statechanger.rfc;

import com.google.common.base.Optional;

import java.util.List;

import javax.inject.Inject;

import co.fusionx.relay.core.Nick;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelNickChangeEvent;
import co.fusionx.relay.event.channel.ChannelWorldNickChangeEvent;
import co.fusionx.relay.event.server.ServerNickChangeEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.rfc.NickParser;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class NickStateChanger implements CommandParser, NickParser.NickObserver {

    private final CommandParser mNickParser;

    private final InternalServer mInternalServer;

    private final InternalUserChannelGroup mUserChannelGroup;

    @Inject
    public NickStateChanger(final InternalServer internalServer,
            final InternalUserChannelGroup userChannelGroup) {
        mInternalServer = internalServer;
        mUserChannelGroup = userChannelGroup;

        // This is intentionally not injected since JoinParser is so straightforward and adds
        // more boilerplate than should be needed
        mNickParser = new NickParser().addObserver(this);
    }

    @Override
    public void onNick(final String prefix, final String newNick) {
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
            final Nick oldNick = user.getNick();
            user.setNick(newNick);

            if (appUser) {
                mInternalServer.postEvent(new ServerNickChangeEvent(mInternalServer, oldNick,
                        user));
            }

            for (final InternalChannel channel : user.getChannels()) {
                final ChannelEvent event = appUser
                        ? new ChannelNickChangeEvent(channel, oldNick, mUserChannelGroup.getUser())
                        : new ChannelWorldNickChangeEvent(channel, oldNick, user);
                channel.postEvent(event);
            }
        }, () -> LogUtils.logOptionalBug(mInternalServer.getConfiguration()));
    }

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        mNickParser.parseCommand(parsedArray, prefix);
    }

}