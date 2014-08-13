package com.fusionx.relay;

import com.fusionx.relay.event.query.QueryActionSelfEvent;
import com.fusionx.relay.event.query.QueryActionWorldEvent;
import com.fusionx.relay.event.query.QueryEvent;
import com.fusionx.relay.event.query.QueryMessageSelfEvent;
import com.fusionx.relay.event.query.QueryMessageWorldEvent;
import com.fusionx.relay.event.query.QueryOpenedEvent;
import com.fusionx.relay.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class RelayQueryUser implements QueryUser {

    private final RelayServer mServer;

    /**
     * Contains a copy of the messages when the conversation
     */
    private final List<QueryEvent> mBuffer;

    private Nick mNick;

    private boolean mValid;

    public RelayQueryUser(final String nick, final RelayServer server, final String message,
            final boolean action, boolean userSent) {
        mNick = new RelayNick(nick);
        mBuffer = new ArrayList<>();
        mServer = server;

        mBuffer.add(new QueryOpenedEvent(this));

        mValid = true;

        if (!Utils.isNotEmpty(message)) {
            return;
        }
        final QueryEvent event;
        if (userSent) {
            if (action) {
                event = new QueryActionSelfEvent(this, server.getUser(), message);
            } else {
                event = new QueryMessageSelfEvent(this, server.getUser(), message);
            }
        } else {
            if (action) {
                event = new QueryActionWorldEvent(this, message);
            } else {
                event = new QueryMessageWorldEvent(this, message);
            }
        }
        mBuffer.add(event);
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
}
