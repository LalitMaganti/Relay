package co.fusionx.relay.parser.ircv3;

import java.util.List;

import co.fusionx.relay.parser.CommandParser;

public class AccountParser implements CommandParser {

    @Override
    public void parseCommand(final List<String> parsedArray, final String prefix) {
        final String accountName = parsedArray.get(0);

        if (accountName.equals("*")) {
            // The user has logged out
        } else {
            // The user has logged in to the account specified by accountname
        }
    }
}