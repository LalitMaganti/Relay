package co.fusionx.relay.parser.code;

import java.util.List;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.event.server.MotdEvent;
import co.fusionx.relay.misc.RelayConfigurationProvider;
import co.fusionx.relay.util.Utils;

import static co.fusionx.relay.constants.ServerReplyCodes.RPL_MOTD;
import static co.fusionx.relay.constants.ServerReplyCodes.RPL_MOTDSTART;

class MotdParser extends CodeParser {

    MotdParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCode(final int code, final List<String> parsedArray) {
        if (!RelayConfigurationProvider.getPreferences().isMOTDShown()) {
            return;
        }
        final String message = parsedArray.get(0);
        if (!Utils.isNotEmpty(message)) {
            return;
        }
        final MotdEvent event;
        if (code == RPL_MOTDSTART || code == RPL_MOTD) {
            final String motdline = message.substring(1).trim();
            event = new MotdEvent(mServer, motdline);
        } else {
            event = new MotdEvent(mServer, message);
        }
        mServer.postAndStoreEvent(event);
    }
}