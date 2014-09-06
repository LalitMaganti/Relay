package co.fusionx.relay.sender.relay;

import android.text.TextUtils;

import co.fusionx.relay.base.relay.RelayQueryUser;
import co.fusionx.relay.packet.user.PrivateActionPacket;
import co.fusionx.relay.packet.user.PrivateMessagePacket;
import co.fusionx.relay.sender.QuerySender;

public class RelayQuerySender implements QuerySender {

    private final RelayQueryUser mQueryUser;

    private final RelayPacketSender mCallHandler;

    public RelayQuerySender(final RelayQueryUser queryUser,
            final RelayPacketSender callHandler) {
        mQueryUser = queryUser;
        mCallHandler = callHandler;
    }

    @Override
    public void sendAction(final String action) {
        if (TextUtils.isEmpty(action)) {
            return;
        }
        mCallHandler.sendPacket(new PrivateActionPacket(mQueryUser.getNick().getNickAsString(),
                action));
    }

    @Override
    public void sendMessage(final String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mCallHandler.sendPacket(new PrivateMessagePacket(mQueryUser.getNick().getNickAsString(),
                message));
    }

    @Override
    public void close() {
        // We don't need to send anything to the server to close a query
    }
}