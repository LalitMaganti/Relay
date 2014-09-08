package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;

public class ErrorCommandParser extends CommandParser {

    private boolean mQuit;

    public ErrorCommandParser(final RelayServer server,
            final RelayUserChannelGroup ucmanager,
            final RelayQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);

        mQuit = false;
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        mQuit = true;
    }

    @Override
    public boolean isUserQuit() {
        return mQuit;
    }
}
