package com.fusionx.relay;

import com.fusionx.relay.event.user.PrivateActionEvent;
import com.fusionx.relay.event.user.PrivateMessageEvent;
import com.fusionx.relay.event.user.UserEvent;
import com.fusionx.relay.event.user.WorldPrivateActionEvent;
import com.fusionx.relay.event.user.WorldPrivateMessageEvent;
import com.fusionx.relay.interfaces.Conversation;
import com.fusionx.relay.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class PrivateMessageUser implements Conversation<UserEvent> {

    private final Server mServer;

    /**
     * Contains a copy of the messages when the conversation
     */
    private final List<UserEvent> mBuffer;

    private Nick mNick;

    /**
     * Retains whether the person on the other end of the PM quit in the middle of the conversation
     */
    private boolean mUserQuit;

    PrivateMessageUser(final String nick, final List<UserEvent> buffer) {
        mNick = new Nick(nick);
        mBuffer = buffer;
        mServer = null;
    }

    public PrivateMessageUser(final String nick, final UserChannelInterface userChannelInterface,
            final String message, final boolean action, boolean userSent) {
        mNick = new Nick(nick);
        mBuffer = new ArrayList<>();
        mServer = userChannelInterface.getServer();

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
    @Override
    public List<UserEvent> getBuffer() {
        return mBuffer;
    }

    @Override
    public String getId() {
        return getNick();
    }

    @Override
    public Server getServer() {
        return mServer;
    }

    public boolean isUserQuit() {
        return mUserQuit;
    }

    public void setUserQuit(boolean userQuit) {
        mUserQuit = userQuit;
    }

    // Nick delegates
    public String getColorfulNick() {
        return mNick.getColorfulNick();
    }

    public String toString() {
        return mNick.toString();
    }

    public String getNick() {
        return mNick.getNick();
    }

    public void setNick(final String nick) {
        mNick = new Nick(nick);
    }
}
