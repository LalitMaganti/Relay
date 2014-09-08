package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.base.Server;
import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;

public abstract class CommandParser {

    final RelayUserChannelGroup mUCManager;

    final RelayQueryUserGroup mQueryManager;

    final Server mServer;

    CommandParser(final Server server, final RelayUserChannelGroup dao,
            final RelayQueryUserGroup queryManager) {
        mServer = server;
        mUCManager = dao;
        mQueryManager = queryManager;
    }

    public abstract void onParseCommand(final List<String> parsedArray, final String prefix);

    public boolean isUserQuit() {
        return false;
    }
}