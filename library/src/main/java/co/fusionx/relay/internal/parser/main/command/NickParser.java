package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.Nick;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelNickChangeEvent;
import co.fusionx.relay.event.channel.ChannelWorldNickChangeEvent;
import co.fusionx.relay.event.server.ServerNickChangeEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class NickParser extends CommandParser {

    public NickParser(final RelayServer server, final RelayUserChannelDao dao) {
        super(server, dao);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String oldRawNick = ParseUtils.getNickFromPrefix(prefix);
        final boolean appUser = mUser.isNickEqual(oldRawNick);
        final Optional<RelayChannelUser> optUser = appUser
                ? Optional.of(mUser)
                : mDao.getUser(oldRawNick);

        // The can happen in cases where gave a nick to the server but it ignored this nick and
        // gave use another one instead. Then half way through the server notice phase it
        // randomly decides to change our nick from the one we provided to the one which we have
        // already been given and using - simply ignore this bad nick change - Miau is a BNC
        // which displays this behaviour
        LogUtils.logOptionalBug(optUser, mServer);
        Optionals.ifPresent(optUser, user -> {
            final String newNick = parsedArray.get(0);
            final Nick oldNick = user.getNick();
            user.setNick(newNick);

            if (appUser) {
                mServer.getBus().post(new ServerNickChangeEvent(mServer, oldNick, user));
            }

            for (final RelayChannel channel : user.getChannels()) {
                final ChannelEvent event = appUser
                        ? new ChannelNickChangeEvent(channel, oldNick, mUser)
                        : new ChannelWorldNickChangeEvent(channel, oldNick, user);
                channel.getBus().post(event);
            }
        });
    }
}