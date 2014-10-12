package co.fusionx.relay.internal.statechanger.rfc;

import co.fusionx.relay.event.server.WallopsEvent;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.parser.rfc.WallopsParser;
import co.fusionx.relay.util.ParseUtils;

public class WallopsStateChanger implements WallopsParser.WallopsObserver {

    private final InternalServer mServer;

    public WallopsStateChanger(final InternalServer server) {
        mServer = server;
    }

    @Override
    public void onWallops(final String prefix, final String message) {
        // It is unlikely that the person who sent the wallops is in one of our channels - simply
        // send the nick and message rather than the spruced up nick
        final String sendingNick = ParseUtils.getNickFromPrefix(prefix);

        mServer.postEvent(new WallopsEvent(mServer, message, sendingNick));
    }
}