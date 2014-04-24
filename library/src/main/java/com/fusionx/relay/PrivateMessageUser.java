package com.fusionx.relay;

import com.fusionx.relay.event.user.PrivateActionEvent;
import com.fusionx.relay.event.user.PrivateMessageEvent;
import com.fusionx.relay.event.user.PrivateMessageOpenedEvent;
import com.fusionx.relay.event.user.UserEvent;
import com.fusionx.relay.event.user.WorldPrivateActionEvent;
import com.fusionx.relay.event.user.WorldPrivateMessageEvent;
import com.fusionx.relay.interfaces.Conversation;
import com.fusionx.relay.nick.BasicNick;
import com.fusionx.relay.nick.Nick;
import com.fusionx.relay.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class PrivateMessageUser implements Conversation {

    private final Server mServer;

    /**
     * Contains a copy of the messages when the conversation
     */
    private final List<UserEvent> mBuffer;

    private Nick mNick;

    public PrivateMessageUser(final String nick, final UserChannelInterface userChannelInterface,
            final String message, final boolean action, boolean userSent) {
        mNick = new BasicNick(nick);
        mBuffer = new ArrayList<>();
        mServer = userChannelInterface.getServer();

        mBuffer.add(new PrivateMessageOpenedEvent(this));

        if (Utils.isNotEmpty(message)) {
            final UserEvent event;
            if (userSent) {
                if (action) {
                    event = new PrivateActionEvent(this, mServer.getUser(), message);
                } else {
                    event = new PrivateMessageEvent(this, mServer.getUser(), message);
                }
            } else {
                if (action) {
                    event = new WorldPrivateActionEvent(this, message);
                } else {
                    event = new WorldPrivateMessageEvent(this, message);
                }
            }
            mBuffer.add(event);
        }
    }

    public void onUserEvent(final UserEvent event) {
        mBuffer.add(event);
    }

    // Getters and Setters
    public List<UserEvent> getBuffer() {
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
