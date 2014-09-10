package co.fusionx.relay.internal.parser;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import co.fusionx.relay.event.server.MotdEvent;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;

import static co.fusionx.relay.internal.constants.ServerReplyCodes.RPL_MOTD;
import static co.fusionx.relay.internal.constants.ServerReplyCodes.RPL_MOTDSTART;

public class MotdParser extends CodeParser {

    public MotdParser(final InternalServer server,
            final InternalUserChannelGroup userChannelInterface,
            final InternalQueryUserGroup queryManager) {
        super(server, userChannelInterface, queryManager);
    }

    @Override
    public void onParseCode(final List<String> parsedArray, final int code) {
        final String message = parsedArray.get(0);
        if (StringUtils.isEmpty(message)) {
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