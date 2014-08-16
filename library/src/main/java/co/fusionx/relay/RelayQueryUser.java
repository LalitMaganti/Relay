package co.fusionx.relay;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.event.query.QueryActionSelfEvent;
import co.fusionx.relay.event.query.QueryClosedEvent;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.event.query.QueryMessageSelfEvent;
import co.fusionx.relay.event.query.QueryOpenedEvent;
import co.fusionx.relay.sender.QuerySender;
import co.fusionx.relay.sender.RelayQuerySender;
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
        mBuffer = new ArrayList<>();
        mServer = server;

        mQuerySender = new RelayQuerySender(this, server.getServerCallHandler());

        mBuffer.add(new QueryOpenedEvent(this));

        mValid = true;
    }

    public void postAndStoreEvent(final QueryEvent queryEvent) {
        onUserEvent(queryEvent);
        mServer.getServerEventBus().post(queryEvent);
    }

    public void onUserEvent(final QueryEvent event) {
        mBuffer.add(event);
    }

    @Override
    public boolean isValid() {
        return mValid;
    }

    public void markInvalid() {
        mValid = false;
    }

    // Getters and Setters
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
        mServer.getUserChannelInterface().removeQueryUser(this);

        postAndStoreEvent(new QueryClosedEvent(this));
    }
}
