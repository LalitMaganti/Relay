package co.fusionx.relay.base.relay;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.base.Nick;
import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.event.query.QueryActionSelfEvent;
import co.fusionx.relay.event.query.QueryClosedEvent;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.event.query.QueryMessageSelfEvent;
import co.fusionx.relay.event.query.QueryOpenedEvent;
import co.fusionx.relay.sender.QuerySender;
import co.fusionx.relay.sender.relay.RelayQuerySender;
import co.fusionx.relay.util.Utils;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayQueryUser implements QueryUser {

    private final RelayServer mServer;

    private final QuerySender mQuerySender;

    /**
     * Contains a copy of the messages when the conversation
     */
    private final List<QueryEvent> mBuffer;

    private Nick mNick;

    private boolean mValid;

    public RelayQueryUser(final String nick, final RelayServer server) {
        mNick = new RelayNick(nick);
        mServer = server;

        mBuffer = new ArrayList<>();
        mBuffer.add(new QueryOpenedEvent(this));

        mQuerySender = new RelayQuerySender(this, server.getRelayPacketSender());

        // This QueryUser is valud until closed
        mValid = true;
    }

    public void postAndStoreEvent(final QueryEvent queryEvent) {
        mBuffer.add(queryEvent);
        mServer.getServerEventBus().post(queryEvent);
    }

    @Override
    public boolean isValid() {
        return mValid;
    }

    public void markInvalid() {
        mValid = false;
    }

    @Override
    public List<QueryEvent> getBuffer() {
        return mBuffer;
    }

    @Override
    public String getId() {
        return mNick.getNickAsString();
    }

    @Override
    public Server getServer() {
        return mServer;
    }

    // Nick delegates
    @Override
    public Nick getNick() {
        return mNick;
    }

    public void setNick(final String nick) {
        mNick = new RelayNick(nick);
    }

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

    // QuerySender interface
    @Override
    public void sendAction(final String action) {
        mQuerySender.sendAction(action);

        if (!Utils.isNotEmpty(action) || getPreferences().isSelfEventHidden()) {
            return;
        }
        postAndStoreEvent(new QueryActionSelfEvent(this, mServer.getUser(), action));
    }

    @Override
    public void sendMessage(final String message) {
        mQuerySender.sendMessage(message);

        if (!Utils.isNotEmpty(message) || getPreferences().isSelfEventHidden()) {
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
}
