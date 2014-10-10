package co.fusionx.relay.internal.statechanger;

import com.google.common.base.Optional;

import java.util.Collection;

import co.fusionx.relay.constant.UserLevel;
import co.fusionx.relay.event.channel.ChannelWorldQuitEvent;
import co.fusionx.relay.event.query.QueryQuitWorldEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.function.Optionals;
import co.fusionx.relay.parser.rfc.QuitParser;
import co.fusionx.relay.util.ParseUtils;

public class QuitStateChanger implements QuitParser.QuitObserver {

    private final InternalUserChannelGroup mUserChannelGroup;

    private final InternalQueryUserGroup mQueryManager;

    private boolean mIsUserQuit;

    public QuitStateChanger(final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryManager) {
        mUserChannelGroup = userChannelGroup;
        mQueryManager = queryManager;
    }

    @Override
    public void onQuit(final String prefix, final Optional<String> optionalReason) {
        final String nick = ParseUtils.getNickFromPrefix(prefix);
        if (mUserChannelGroup.getUser().isNickEqual(nick)) {
            onQuit();
        } else {
            onUserQuit(nick, optionalReason);
        }
    }

    private void onUserQuit(final String userNick, final Optional<String> optionalReason) {
        final Optional<InternalChannelUser> optUser = mUserChannelGroup.getUser(userNick);
        Optionals.ifPresent(optUser, user -> {
            final Collection<InternalChannel> channels = mUserChannelGroup.removeUser(user);
            for (final InternalChannel channel : channels) {
                final UserLevel level = user.getChannelPrivileges(channel);
                mUserChannelGroup.removeUserFromChannel(channel, user);
                channel.postEvent(new ChannelWorldQuitEvent(channel, user, level, optionalReason));
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