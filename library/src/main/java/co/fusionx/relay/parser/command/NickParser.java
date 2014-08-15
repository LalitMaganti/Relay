package co.fusionx.relay.parser.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.Nick;
import co.fusionx.relay.RelayChannel;
import co.fusionx.relay.RelayChannelUser;
import co.fusionx.relay.RelayMainUser;
import co.fusionx.relay.RelayServer;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelNickChangeEvent;
import co.fusionx.relay.event.channel.ChannelWorldNickChangeEvent;
import co.fusionx.relay.event.server.ServerNickChangeEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.LogUtils;

class NickParser extends CommandParser {

    private static final int NEW_NICK_INDEX = 2;

    public NickParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String oldRawNick = IRCUtils.getNickFromRaw(rawSource);
        final boolean appUser = mServer.getUser().isNickEqual(oldRawNick);
        final Optional<RelayChannelUser> optUser = appUser
                ? Optional.of(mServer.getUser())
                : mUserChannelInterface.getUser(oldRawNick);

        // The can happen in cases where gave a nick to the server but it ignored this nick and
        // gave use another one instead. Then half way through the server notice phase it
        // randomly decides to change our nick from the one we provided to the one which we have
        // already been given and using - simply ignore this bad nick change - Miau is a BNC
        // which displays this behaviour
        LogUtils.logOptionalBug(optUser, mServer);
        Optionals.ifPresent(optUser, user -> {
            final String newNick = parsedArray.get(NEW_NICK_INDEX);
            final Nick oldNick = user.getNick();
            user.setNick(newNick);

            if (appUser) {
                final ServerNickChangeEvent event = new ServerNickChangeEvent(mServer, oldNick,
                        user);
                mServerEventBus.postAndStoreEvent(event);
            }

            for (final RelayChannel channel : user.getChannels()) {
                final ChannelEvent event = appUser
                        ? new ChannelNickChangeEvent(channel, oldNick, (RelayMainUser) user)
                        : new ChannelWorldNickChangeEvent(channel, oldNick, user);
                mServerEventBus.postAndStoreEvent(event, channel);
            }
        });
    }
}