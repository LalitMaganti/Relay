package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.constants.UserListChangeType;

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
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        if (user.isUserNickEqual(mServer.getUser())) {
            // TODO - improve this
            mIsUserQuit = true;
        } else {
            final Set<Channel> list = mUserChannelInterface.removeUser(user);
            for (final Channel channel : list) {
                final String reason = parsedArray.size() == 4 ? parsedArray.get(3).replace("\"",
                        "") : "";
                final String nick = user.getPrettyNick(channel);

                channel.onDecrementUserType(user.getChannelPrivileges(channel));

                final String message = mEventResponses.getQuitMessage(nick, reason);
                if (channel.isObserving()) {
                    mServerEventBus.sendGenericChannelEvent(channel, message,
                            UserListChangeType.REMOVE, user);
                } else {
                    mUserChannelInterface.removeUserFromChannel(channel, user);
                    mServerEventBus.sendGenericChannelEvent(channel, message,
                            UserListChangeType.REMOVE);
                }
            }
        }
    }

    public boolean isUserQuit() {
        return mIsUserQuit;
    }
}