package co.fusionx.relay.internal.parser.main.code;

import android.util.Pair;

import java.util.List;

import co.fusionx.relay.internal.base.RelayChannel;
import co.fusionx.relay.internal.base.RelayChannelUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.constants.UserLevel;
import co.fusionx.relay.event.channel.ChannelNameEvent;
import co.fusionx.relay.internal.base.RelayUserChannelDao;
import co.fusionx.relay.util.ParseUtils;

import static co.fusionx.relay.internal.constants.ServerReplyCodes.RPL_NAMREPLY;
import static co.fusionx.relay.util.IRCv3Utils.consumeNickPrefixes;

public class NameParser extends CodeParser {

    private RelayChannel mChannel;

    public NameParser(final RelayServer server,
            final RelayUserChannelDao userChannelInterface) {
        super(server, userChannelInterface);
    }

    @Override
    public void onParseCode(final List<String> parsedArray, final int code) {
        if (code == RPL_NAMREPLY) {
            onParseNameReply(parsedArray);
        } else {
            onParseNameFinished();
        }
    }

    private void onParseNameReply(final List<String> parsedArray) {
        if (mChannel == null) {
            // TODO - this needs to be handled properly rather than simply getting
            final String channelName = parsedArray.get(1);
            mChannel = mUserChannelInterface.getChannel(channelName).get();
        }

        final String users = parsedArray.get(2);
        final List<String> listOfUsers = ParseUtils.splitRawLine(users, false);

        for (final String rawNick : listOfUsers) {
            final Pair<String, UserLevel> pair = consumeNickPrefixes(mServer, rawNick);
            final RelayChannelUser user = mUserChannelInterface.getNonNullUser(pair.first);
            mUserChannelInterface.coupleUserAndChannel(user, mChannel, pair.second);
        }
    }

    private void onParseNameFinished() {
        mChannel.postAndStoreEvent(new ChannelNameEvent(mChannel, mChannel.getUsers()));
        mChannel = null;
    }
}