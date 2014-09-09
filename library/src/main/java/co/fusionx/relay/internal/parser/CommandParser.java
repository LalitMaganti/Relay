package co.fusionx.relay.internal.parser;

import java.util.List;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;

public abstract class CommandParser {

    final InternalUserChannelGroup mUserChannelGroup;

    final InternalQueryUserGroup mQueryManager;

    final Server mServer;

    CommandParser(final Server server, final InternalUserChannelGroup dao,
            final InternalQueryUserGroup queryManager) {
        mServer = server;
        mUserChannelGroup = dao;
        mQueryManager = queryManager;
    }

    public abstract void onParseCommand(final List<String> parsedArray, final String prefix);

    public boolean isUserQuit() {
        return false;
    }
}