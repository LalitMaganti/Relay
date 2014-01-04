package com.fusionx.relay.parser.code;

import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.WorldUser;
import com.fusionx.relay.constants.ServerReplyCodes;

import java.util.List;

import static com.fusionx.relay.constants.ServerReplyCodes.RPL_ENDOFWHO;

public class WhoParser extends CodeParser {

    private final UserChannelInterface mUserChannelInterface;

    private Channel mWhoChannel;

    WhoParser(final Server server) {
        super(server);

        mUserChannelInterface = server.getUserChannelInterface();
    }

    private void onParseWho(final List<String> parsedArray) {
        if (mWhoChannel == null) {
            mWhoChannel = mUserChannelInterface.getChannel(parsedArray.get(0));
        }
        final WorldUser user = mUserChannelInterface.getUser(parsedArray.get(4));
        user.onWhoMode(parsedArray.get(5), mWhoChannel);
    }

    private void onParseWhoFinished() {
        if (mWhoChannel != null && mWhoChannel.getUsers() != null) {
            //mServer.getServerEventBus().post(new NameEvent(mWhoChannel, mWhoChannel.getUsers()));
            //final Event event = mServer.getServerEventBus().onNameFinished(mWhoChannel,
            //        mWhoChannel.getUsers());
            mWhoChannel = null;
        }
    }

    @Override
    public void onParseCode(final int code, final List<String> parsedArray) {
        if (code == ServerReplyCodes.RPL_WHOREPLY) {
            onParseWho(parsedArray);
        } else if (code == RPL_ENDOFWHO) {
            onParseWhoFinished();
        }
    }
}
