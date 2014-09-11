package co.fusionx.relay.internal.parser;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelWorldQuitEvent;
import co.fusionx.relay.event.query.QueryQuitWorldEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.util.ParseUtils;

public class QuitParser extends CommandParser {

    private boolean mIsUserQuit;

    public QuitParser(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryManager) {
        super(server, userChannelGroup, queryManager);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String nick = ParseUtils.getNickFromPrefix(prefix);
        if (mUserChannelGroup.getUser().isNickEqual(nick)) {
            onQuit();
        } else {
            onUserQuit(parsedArray, nick);
        }
    }

    public boolean isUserQuit() {
        return mIsUserQuit;
    }

    private void onUserQuit(final List<String> parsed, final String userNick) {
        final Optional<InternalChannelUser> optUser = mUserChannelGroup.getUser(userNick);
        Optionals.ifPresent(optUser, user -> {
            final Collection<InternalChannel> channels = mUserChannelGroup.removeUser(user);
            final String reason = parsed.size() == 2 ? parsed.get(1).replace("\"", "") : "";
            for (final InternalChannel channel : channels) {
                final UserLevel level = user.getChannelPrivileges(channel);
                mUserChannelGroup.removeUserFromChannel(channel, user);
                channel.postEvent(new ChannelWorldQuitEvent(channel, user, level, reason));
            }
        });

        final Optional<InternalQueryUser> optQuery = mQueryManager.getQueryUser(userNick);
        Optionals.ifPresent(optQuery,
                queryUser -> queryUser.postEvent(new QueryQuitWorldEvent(queryUser)));
    }

    private void onQuit() {
        // TODO - improve this
        mIsUserQuit = true;
    }
}