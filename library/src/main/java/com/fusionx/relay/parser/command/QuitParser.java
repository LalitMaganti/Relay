package com.fusionx.relay.parser.command;

import com.fusionx.relay.Channel;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.event.channel.WorldQuitEvent;
import com.fusionx.relay.event.user.WorldPrivateQuitEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.Collection;
import java.util.List;

public class QuitParser extends CommandParser {

    private boolean mIsUserQuit;

    public QuitParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        final WorldUser user = mUserChannelInterface.getUserIfExists(nick);
        if (mServer.getUser().isUserNickEqual(user)) {
            onQuit();
        } else {
            onUserQuit(parsedArray, user);
        }
    }

    private void onUserQuit(final List<String> parsedArray, final WorldUser user) {
        final Collection<Channel> list = mUserChannelInterface.removeUser(user);
        final String reason = parsedArray.size() == 4 ? parsedArray.get(3).replace("\"", "") : "";
        for (final Channel channel : list) {
            mUserChannelInterface.removeUserFromChannel(channel, user);

            final WorldQuitEvent event = new WorldQuitEvent(channel, user, reason);
            mServerEventBus.postAndStoreEvent(event, channel);
        }

        final PrivateMessageUser pmUser = mUserChannelInterface.getPrivateMessageUser(user
                .getNick());
        if (pmUser != null) {
            pmUser.setUserQuit(true);

            final WorldPrivateQuitEvent event = new WorldPrivateQuitEvent(pmUser);
            mServerEventBus.postAndStoreEvent(event, pmUser);
        }
    }

    private void onQuit() {
        // TODO - improve this
        mIsUserQuit = true;
    }

    public boolean isUserQuit() {
        return mIsUserQuit;
    }
}