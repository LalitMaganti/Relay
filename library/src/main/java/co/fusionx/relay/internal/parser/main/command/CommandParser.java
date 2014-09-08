package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.base.Server;
import co.fusionx.relay.internal.base.RelayLibraryUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;

public abstract class CommandParser {

    final RelayUserChannelDao mDao;

    final Server mServer;

    final RelayLibraryUser mUser;

    CommandParser(final Server server, final RelayUserChannelDao dao) {
        mServer = server;
        mDao = dao;
        mUser = dao.getUser();
    }

    public abstract void onParseCommand(final List<String> parsedArray, final String prefix);

    public boolean isUserQuit() {
        return false;
    }
}