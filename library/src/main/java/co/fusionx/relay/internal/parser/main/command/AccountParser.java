package co.fusionx.relay.internal.parser.main.command;

import android.util.Log;

import java.util.List;

import co.fusionx.relay.internal.base.RelayServer;

public class AccountParser extends CommandParser {

    AccountParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        final String accountName = parsedArray.get(0);
        Log.e("Relay", "ACCOUNT: " + accountName);

        if (accountName.equals("*")) {
            // The user has logged out
        } else {
            // The user has logged in to the account specified by accountname
        }
    }
}