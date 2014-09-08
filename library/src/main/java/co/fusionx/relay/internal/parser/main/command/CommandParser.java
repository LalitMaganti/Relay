package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayLibraryUser;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;

public abstract class CommandParser {

    final RelayUserChannelDao mUserChannelInterface;

    final RelayServer mServer;

    final RelayLibraryUser mUser;

    CommandParser(final RelayServer server,
            final RelayUserChannelDao userChannelInterface) {
        mServer = server;
        mUserChannelInterface = userChannelInterface;
        mUser = userChannelInterface.getUser();
    }

    public abstract void onParseCommand(final List<String> parsedArray, final String prefix);
}