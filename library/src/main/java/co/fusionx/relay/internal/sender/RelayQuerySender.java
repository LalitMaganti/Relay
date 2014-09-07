package co.fusionx.relay.internal.sender;

import android.text.TextUtils;

import co.fusionx.relay.internal.base.RelayQueryUser;
import co.fusionx.relay.internal.packet.query.QueryActionPacket;
import co.fusionx.relay.internal.packet.query.QueryMessagePacket;
import co.fusionx.relay.sender.QuerySender;

public class RelayQuerySender implements QuerySender {

    private final RelayQueryUser mQueryUser;

    private final BaseSender mSender;

    public RelayQuerySender(final RelayQueryUser queryUser, final BaseSender sender) {
        mQueryUser = queryUser;
        mSender = sender;
    }

    @Override
    public void sendAction(final String action) {
        if (TextUtils.isEmpty(action)) {
            return;
        }
        mSender.sendPacket(new QueryActionPacket(mQueryUser.getNick().getNickAsString(), action));
    }

    @Override
    public void sendMessage(final String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mSender.sendPacket(new QueryMessagePacket(mQueryUser.getNick().getNickAsString(), message));
    }

    @Override
    public void close() {
        // We don't need to send anything to the server to close a query
    }
}