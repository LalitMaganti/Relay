package co.fusionx.relay.internal.statechanger.rfc;

import co.fusionx.relay.event.server.InviteEvent;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.parser.rfc.InviteParser;
import co.fusionx.relay.util.ParseUtils;

public class InviteStateChanger implements InviteParser.InviteObserver {

    private final InternalServer mServer;

    public InviteStateChanger(final InternalServer server) {
        mServer = server;
    }

    @Override
    public void onInvite(final String invitingPrefix, final String invitedNick,
            final String channelName) {
        final String invitingNick = ParseUtils.getNickFromPrefix(invitingPrefix);

        mServer.postEvent(new InviteEvent(mServer, channelName, invitingNick));
    }
}