package co.fusionx.relay.internal.parser.main.code;

import java.util.List;

import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;

public abstract class CodeParser {

    final InternalUserChannelGroup mUserChannelInterface;

    final InternalServer mServer;

    final InternalQueryUserGroup mQueryManager;

    CodeParser(final InternalServer server,
            final InternalUserChannelGroup userChannelInterface,
            final InternalQueryUserGroup queryManager) {
        mServer = server;
        mUserChannelInterface = userChannelInterface;
        mQueryManager = queryManager;
    }

    public abstract void onParseCode(final List<String> parsedArray, final int code);
}