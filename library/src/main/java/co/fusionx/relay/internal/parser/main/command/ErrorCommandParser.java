package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.RelayUserChannelDao;

public class ErrorCommandParser extends CommandParser {

    private boolean mQuit;

    public ErrorCommandParser(final RelayServer server, final RelayUserChannelDao dao) {
        super(server, dao);

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
