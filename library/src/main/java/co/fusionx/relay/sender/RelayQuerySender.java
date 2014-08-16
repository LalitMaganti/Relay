package co.fusionx.relay.sender;

import co.fusionx.relay.RelayQueryUser;
import co.fusionx.relay.bus.ServerCallHandler;
import co.fusionx.relay.call.user.PrivateActionCall;
import co.fusionx.relay.call.user.PrivateMessageCall;
import co.fusionx.relay.util.Utils;

public class RelayQuerySender implements QuerySender {

    private final RelayQueryUser mQueryUser;

    private final ServerCallHandler mCallHandler;

    public RelayQuerySender(final RelayQueryUser queryUser, final ServerCallHandler callHandler) {
        mQueryUser = queryUser;
        mCallHandler = callHandler;
    }

    @Override
    public void sendAction(final String action) {
        if (!Utils.isNotEmpty(action)) {
            return;
        }
        mCallHandler.post(new PrivateActionCall(mQueryUser.getNick().getNickAsString(), action));
    }

    @Override
    public void sendMessage(final String message) {
        if (!Utils.isNotEmpty(message)) {
            return;
        }
        mCallHandler.post(new PrivateMessageCall(mQueryUser.getNick().getNickAsString(), message));
    }

    @Override
    public void close() {

    }
}