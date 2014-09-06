package co.fusionx.relay.base.relay;

import android.text.TextUtils;

import co.fusionx.relay.base.Nick;
import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.event.query.QueryActionSelfEvent;
import co.fusionx.relay.event.query.QueryClosedEvent;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.event.query.QueryMessageSelfEvent;
import co.fusionx.relay.sender.QuerySender;
import co.fusionx.relay.sender.relay.RelayQuerySender;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayQueryUser extends RelayAbstractConversation<QueryEvent> implements QueryUser {

    private final Nick mNick;

    private final QuerySender mQuerySender;

    public RelayQueryUser(final String nick, final RelayServer server) {
        super(server);

        mNick = new RelayNick(nick);

        mQuerySender = new RelayQuerySender(this, server.getRelayPacketSender());
    }

    @Override
    public String getId() {
        return mNick.getNickAsString();
    }

    @Override
    public Nick getNick() {
        return mNick;
    }

    // QuerySender interface
    @Override
    public void sendAction(final String action) {
        mQuerySender.sendAction(action);

        if (TextUtils.isEmpty(action) || getPreferences().isSelfEventHidden()) {
            return;
        }
        postAndStoreEvent(new QueryActionSelfEvent(this, mServer.getUser(), action));
    }

    @Override
    public void sendMessage(final String message) {
        mQuerySender.sendMessage(message);

        if (TextUtils.isEmpty(message) || getPreferences().isSelfEventHidden()) {
            return;
        }
        postAndStoreEvent(new QueryMessageSelfEvent(this, mServer.getUser(), message));
    }

    @Override
    public void close() {
        mQuerySender.close();

        mServer.getUserChannelInterface().removeQueryUser(this);
        postAndStoreEvent(new QueryClosedEvent(this));
    }

    // Equals and hashcode
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof RelayQueryUser)) {
            return false;
        }

        final RelayQueryUser user = (RelayQueryUser) o;
        return mNick.equals(user.mNick) && mServer.equals(user.mServer);
    }

    @Override
    public int hashCode() {
        int result = mServer.hashCode();
        result = 31 * result + mNick.hashCode();
        return result;
    }
}
