package com.fusionx.relay.parser.code;

import com.fusionx.relay.Server;
import com.fusionx.relay.event.server.MotdEvent;
import com.fusionx.relay.misc.InterfaceHolders;

import java.util.List;

import static com.fusionx.relay.constants.ServerReplyCodes.RPL_MOTD;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_MOTDSTART;

public class MotdParser extends CodeParser {

    MotdParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCode(final int code, final List<String> parsedArray) {
        if (InterfaceHolders.getPreferences().isMOTDShown()) {
            final String message = parsedArray.get(0);
            final MotdEvent event;
            if (code == RPL_MOTDSTART || code == RPL_MOTD) {
                final String motdline = message.substring(1).trim();
                event = new MotdEvent(motdline);
            } else {
                event = new MotdEvent(message);
            }
            mServerEventBus.postAndStoreEvent(event, mServer);
        }
    }
}