package co.fusionx.relay.internal.parser.main.code;

import java.util.List;

import co.fusionx.relay.internal.base.RelayLibraryUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;

public abstract class CodeParser {

    final RelayUserChannelDao mUserChannelInterface;

    final RelayLibraryUser mUser;

    final RelayServer mServer;

    CodeParser(final RelayServer server, final RelayUserChannelDao userChannelInterface) {
        mServer = server;
        mUserChannelInterface = userChannelInterface;
        mUser = userChannelInterface.getUser();
    }

    public abstract void onParseCode(final List<String> parsedArray, final int code);
}