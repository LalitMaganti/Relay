package co.fusionx.relay.internal.parser.main.command;

import java.util.List;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.util.ParseUtils;

public class AwayParser extends CommandParser {

    public AwayParser(final Server server,
            final InternalUserChannelGroup ucmanager,
            final InternalQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String nick = ParseUtils.getNickFromPrefix(prefix);
        if (parsedArray.size() == 0) {
            // The user is no longer away
        } else {
            // The user is now away
            final String awayMessage = parsedArray.get(0);
        }
    }
}
