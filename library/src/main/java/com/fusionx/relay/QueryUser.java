package com.fusionx.relay;

import com.fusionx.relay.event.query.QueryActionSelfEvent;
import com.fusionx.relay.event.query.QueryActionWorldEvent;
import com.fusionx.relay.event.query.QueryEvent;
import com.fusionx.relay.event.query.QueryMessageSelfEvent;
import com.fusionx.relay.event.query.QueryMessageWorldEvent;
import com.fusionx.relay.event.query.QueryOpenedEvent;
import com.fusionx.relay.interfaces.Conversation;
import com.fusionx.relay.nick.BasicNick;
import com.fusionx.relay.nick.Nick;
import com.fusionx.relay.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class QueryUser implements Conversation {

    private final Server mServer;

    /**
     * Contains a copy of the messages when the conversation
     */
    private final List<QueryEvent> mBuffer;

    private Nick mNick;

    public QueryUser(final String nick, final Server server, final String message,
            final boolean action, boolean userSent) {
        mNick = new BasicNick(nick);
        mBuffer = new ArrayList<>();
        mServer = server;

        mBuffer.add(new QueryOpenedEvent(this));

        if (Utils.isNotEmpty(message)) {
            final QueryEvent event;
            event = userSent
                    ? action
                    ? new QueryActionSelfEvent(this, mServer.getUser(), message)
                    : new QueryMessageSelfEvent(this, mServer.getUser(), message)
                    : action
                    ? new QueryActionWorldEvent(this, message)
                    : new QueryMessageWorldEvent(this, message);
            mBuffer.add(event);
        }
    }

    public void onUserEvent(final QueryEvent event) {
        mBuffer.add(event);
    }

    // Getters and Setters
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
    public Nick getNick() {
        return mNick;
    }

    public void setNick(final String nick) {
        mNick = new BasicNick(nick);
    }
}
