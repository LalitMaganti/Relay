package com.fusionx.relay.parser.code;

import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.event.channel.ChannelNameEvent;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

import static com.fusionx.relay.constants.ServerReplyCodes.RPL_NAMREPLY;

class NameParser extends CodeParser {

    private RelayChannel mChannel;

    public NameParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCode(final int code, final List<String> parsedArray) {
        if (code == RPL_NAMREPLY) {
            onParseNameReply(parsedArray);
        } else {
            onParseNameFinished();
        }
    }

    private void onParseNameReply(final List<String> parsedArray) {
        if (mChannel == null) {
            mChannel = mUserChannelInterface.getChannel(parsedArray.get(1));
        }
        final List<String> listOfUsers = IRCUtils.splitRawLine(parsedArray.get(2), false);
        for (final String rawNick : listOfUsers) {
            final UserLevel level = UserLevel.getLevelFromPrefix(rawNick.charAt(0));
            final String nick = level == UserLevel.NONE ? rawNick : rawNick.substring(1);
            final RelayChannelUser user = mUserChannelInterface.getNonNullUser(nick);
            mUserChannelInterface.coupleUserAndChannel(user, mChannel, level);
        }
    }

    private void onParseNameFinished() {
        mServer.getServerEventBus().postAndStoreEvent(new ChannelNameEvent(mChannel,
                mChannel.getUsers()), mChannel);

        mChannel = null;
    }
}