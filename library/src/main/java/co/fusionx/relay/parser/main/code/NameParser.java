package co.fusionx.relay.parser.main.code;

import java.util.List;

import co.fusionx.relay.base.relay.RelayChannel;
import co.fusionx.relay.base.relay.RelayChannelUser;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelNameEvent;
import co.fusionx.relay.util.IRCUtils;

import static co.fusionx.relay.constants.ServerReplyCodes.RPL_NAMREPLY;

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
            // TODO - this needs to be handled properly rather than simply getting
            mChannel = mUserChannelInterface.getChannel(parsedArray.get(1)).get();
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
        mChannel.postAndStoreEvent(new ChannelNameEvent(mChannel, mChannel.getUsers()));
        mChannel = null;
    }
}