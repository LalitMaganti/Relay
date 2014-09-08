package co.fusionx.relay.internal.parser.main.code;

import java.util.List;

import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.event.server.MotdEvent;
import co.fusionx.relay.internal.base.RelayUserChannelDao;
import co.fusionx.relay.misc.RelayConfigurationProvider;
import co.fusionx.relay.util.Utils;

import static co.fusionx.relay.internal.constants.ServerReplyCodes.RPL_MOTD;
import static co.fusionx.relay.internal.constants.ServerReplyCodes.RPL_MOTDSTART;

public class MotdParser extends CodeParser {

    public MotdParser(final RelayServer server,
            final RelayUserChannelDao userChannelInterface) {
        super(server, userChannelInterface);
    }

    @Override
    public void onParseCode(final List<String> parsedArray, final int code) {
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
        mServer.getBus().post(event);
    }
}