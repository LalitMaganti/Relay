package co.fusionx.relay.sender.relay;

import co.fusionx.relay.call.server.NickServPasswordCall;
import co.fusionx.relay.call.server.QuitCall;
import co.fusionx.relay.call.server.UserCall;
import co.fusionx.relay.call.server.internal.PongCall;
import co.fusionx.relay.call.server.internal.ServerPasswordCall;

public class RelayInternalSender {

    private final RelayServerLineSender mRelayServerLineSender;

    public RelayInternalSender(final RelayServerLineSender relayServerLineSender) {
        mRelayServerLineSender = relayServerLineSender;
    }

    public void pongServer(final String source) {
        mRelayServerLineSender.post(new PongCall(source));
    }

    public void sendServerPassword(final String password) {
        mRelayServerLineSender.post(new ServerPasswordCall(password));
    }

    public void sendNickServPassword(final String password) {
        mRelayServerLineSender.post(new NickServPasswordCall(password));
    }

    public void sendUser(final String serverUserName, final String realName) {
        mRelayServerLineSender.post(new UserCall(serverUserName, realName));
    }

    public void quitServer(final String quitReason) {
        mRelayServerLineSender.post(new QuitCall(quitReason));
    }
}