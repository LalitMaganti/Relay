package co.fusionx.relay.internal.parser.main.code;

import java.util.List;

import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;

public abstract class CodeParser {

    final RelayUserChannelGroup mUserChannelInterface;

    final RelayServer mServer;

    final RelayQueryUserGroup mQueryManager;

    CodeParser(final RelayServer server,
            final RelayUserChannelGroup userChannelInterface,
            final RelayQueryUserGroup queryManager) {
        mServer = server;
        mUserChannelInterface = userChannelInterface;
        mQueryManager = queryManager;
    }

    public abstract void onParseCode(final List<String> parsedArray, final int code);
}