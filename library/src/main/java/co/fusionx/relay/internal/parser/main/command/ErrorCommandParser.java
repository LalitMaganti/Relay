package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;

public class ErrorCommandParser extends CommandParser {

    private boolean mQuit;

    public ErrorCommandParser(final InternalServer server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager) {
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
