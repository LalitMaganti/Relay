package co.fusionx.relay.internal.parser.main.command;

import android.util.Log;

import java.util.List;

import co.fusionx.relay.base.Server;
import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayUserChannelGroup;

public class AccountParser extends CommandParser {

    public AccountParser(final Server server,
            final RelayUserChannelGroup ucmanager,
            final RelayQueryUserGroup queryManager) {
        super(server, ucmanager, queryManager);
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