package co.fusionx.relay.internal.statechanger.rfc;

import org.apache.commons.lang3.StringUtils;

import co.fusionx.relay.event.server.MotdEvent;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.parser.rfc.MotdParser;

import static co.fusionx.relay.constant.ReplyCodes.RPL_MOTD;
import static co.fusionx.relay.constant.ReplyCodes.RPL_MOTDSTART;

public class MotdStateChanger implements MotdParser.MotdObserver {

    private final InternalServer mServer;

    public MotdStateChanger(final InternalServer server) {
        mServer = server;
    }

    @Override
    public void onMotd(final int code, final String motdMessage) {
        if (StringUtils.isEmpty(motdMessage)) {
            return;
        }

        final MotdEvent event;
        if (code == RPL_MOTDSTART || code == RPL_MOTD) {
            final String motdline = motdMessage.substring(1).trim();
            event = new MotdEvent(mServer, motdline);
        } else {
            event = new MotdEvent(mServer, motdMessage);
        }
        mServer.postEvent(event);
    }
}