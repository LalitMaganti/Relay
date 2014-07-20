package com.fusionx.relay.parser.command;

import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayQueryUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelWorldQuitEvent;
import com.fusionx.relay.event.query.QueryQuitWorldEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.Collection;
import java.util.List;

public class QuitParser extends CommandParser {

    private boolean mIsUserQuit;

    public QuitParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        final RelayChannelUser user = getUserChannelInterface().getUser(nick);
        if (getServer().getUser().isNickEqual(nick)) {
            onQuit();
        } else {
            onUserQuit(parsedArray, user);
        }
    }

    public boolean isUserQuit() {
        return mIsUserQuit;
    }

    private void onUserQuit(final List<String> parsedArray, final RelayChannelUser user) {
        final Collection<RelayChannel> list = getUserChannelInterface().removeUser(user);
        final String reason = parsedArray.size() == 3 ? parsedArray.get(2).replace("\"", "") : "";
        for (final RelayChannel channel : list) {
            getUserChannelInterface().removeUserFromChannel(channel, user);

            final ChannelWorldQuitEvent event = new ChannelWorldQuitEvent(channel, user, reason);
            getServerEventBus().postAndStoreEvent(event, channel);
        }

        final RelayQueryUser pmUser = getUserChannelInterface().getQueryUser(user.getNick()
                .getNickAsString());
        if (pmUser == null) {
            return;
        }
        final QueryQuitWorldEvent event = new QueryQuitWorldEvent(pmUser);
        getServerEventBus().postAndStoreEvent(event, pmUser);
    }

    private void onQuit() {
        // TODO - improve this
        mIsUserQuit = true;
    }
}