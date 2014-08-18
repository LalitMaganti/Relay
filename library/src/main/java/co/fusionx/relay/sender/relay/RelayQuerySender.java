package co.fusionx.relay.sender.relay;

import co.fusionx.relay.base.relay.RelayQueryUser;
import co.fusionx.relay.call.user.PrivateActionCall;
import co.fusionx.relay.call.user.PrivateMessageCall;
import co.fusionx.relay.sender.QuerySender;
import co.fusionx.relay.util.Utils;

public class RelayQuerySender implements QuerySender {

    private final RelayQueryUser mQueryUser;

    private final RelayServerLineSender mCallHandler;

    public RelayQuerySender(final RelayQueryUser queryUser,
            final RelayServerLineSender callHandler) {
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
        // We don't need to send anything to the server to close a query
    }
}