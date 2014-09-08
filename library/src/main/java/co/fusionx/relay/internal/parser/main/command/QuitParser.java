package co.fusionx.relay.internal.parser.main.command;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelWorldQuitEvent;
import co.fusionx.relay.event.query.QueryQuitWorldEvent;
import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayQueryUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.ParseUtils;

public class QuitParser extends CommandParser {

    private boolean mIsUserQuit;

    public QuitParser(final RelayServer server,
            final RelayUserChannelGroup ucmanager,
            final RelayQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String nick = ParseUtils.getNickFromPrefix(prefix);
        if (mUCManager.getUser().isNickEqual(nick)) {
            onQuit();
        } else {
            onUserQuit(parsedArray, nick);
        }
    }

    public boolean isUserQuit() {
        return mIsUserQuit;
    }

    private void onUserQuit(final List<String> parsed, final String userNick) {
        final Optional<RelayChannelUser> optUser = mUCManager.getUser(userNick);
        Optionals.ifPresent(optUser, user -> {
            final Collection<RelayChannel> channels = mUCManager.removeUser(user);
            final String reason = parsed.size() == 2 ? parsed.get(1).replace("\"", "") : "";
            for (final RelayChannel channel : channels) {
                final UserLevel level = user.getChannelPrivileges(channel);
                mUCManager.removeUserFromChannel(channel, user);
                channel.getBus().post(new ChannelWorldQuitEvent(channel, user, level, reason));
            }
        });

        final Optional<RelayQueryUser> optQuery = mQueryManager.getQueryUser(userNick);
        Optionals.ifPresent(optQuery,
                queryUser -> queryUser.getBus().post(new QueryQuitWorldEvent(queryUser)));
    }

    private void onQuit() {
        // TODO - improve this
        mIsUserQuit = true;
    }
}