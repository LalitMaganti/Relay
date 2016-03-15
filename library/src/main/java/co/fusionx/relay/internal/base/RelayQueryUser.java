package co.fusionx.relay.internal.base;

import android.text.TextUtils;

import co.fusionx.relay.base.Nick;
import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.event.query.QueryActionSelfEvent;
import co.fusionx.relay.event.query.QueryClosedEvent;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.event.query.QueryMessageSelfEvent;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.internal.sender.RelayQuerySender;
import co.fusionx.relay.sender.QuerySender;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayQueryUser extends RelayAbstractConversation<QueryEvent> implements QueryUser {

    private final RelayUserChannelInterface mUserChannelInterface;

    private final RelayMainUser mUser;

    private final Nick mNick;

    private final QuerySender mQuerySender;

    public RelayQueryUser(final Server server, final RelayUserChannelInterface userChannelInterface,
            final BaseSender sender, final String nick) {
        super(server);

        mUserChannelInterface = userChannelInterface;
        mUser = mUserChannelInterface.getMainUser();

        mNick = new RelayNick(nick);
        mQuerySender = new RelayQuerySender(this, sender);
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
        postAndStoreEvent(new QueryActionSelfEvent(this, mUser, action));
    }

    @Override
    public void sendMessage(final String message) {
        mQuerySender.sendMessage(message);

        if (TextUtils.isEmpty(message) || getPreferences().isSelfEventHidden()) {
            return;
        }
        postAndStoreEvent(new QueryMessageSelfEvent(this, mUser, message));
    }

    @Override
    public void close() {
        mQuerySender.close();

        mUserChannelInterface.removeQueryUser(this);
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
