package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.constants.UserListChangeType;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;
import java.util.Set;

public class QuitParser extends CommandParser {

    private boolean mIsUserQuit;

    public QuitParser(Server server) {
        super(server);
    }

    // TODO - split this up
    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        final ChannelUser user = mUserChannelInterface.getUserIfExists(nick);
        if (user.isUserNickEqual(mServer.getUser())) {
            // TODO - improve this
            mIsUserQuit = true;
        } else {
            final Set<Channel> list = mUserChannelInterface.removeUser(user);
            final String reason = parsedArray.size() == 4 ? parsedArray.get(3).replace("\"",
                    "") : "";
            for (final Channel channel : list) {
                final String prettyNick = user.getPrettyNick(channel);
                final String message = mEventResponses.getQuitMessage(prettyNick, reason);

                channel.onDecrementUserType(user.getChannelPrivileges(channel));

                if (channel.isObserving()) {
                    mServerEventBus.sendGenericChannelEvent(channel, message,
                            UserListChangeType.REMOVE, user);
                } else {
                    mUserChannelInterface.removeUserFromChannel(channel, user);
                    mServerEventBus.sendGenericChannelEvent(channel, message,
                            UserListChangeType.REMOVE);
                }
            }

            final PrivateMessageUser privateMessageUser = mServer
                    .getPrivateMessageUserIfExists(nick);
            if (privateMessageUser != null) {
                privateMessageUser.setUserQuit(true);
                final String message = mEventResponses.getQuitMessage(privateMessageUser
                        .getColorfulNick(), reason);
                mServerEventBus.sendPrivateQuitEvent(privateMessageUser, message);
            }
        }
    }

    public boolean isUserQuit() {
        return mIsUserQuit;
    }
}