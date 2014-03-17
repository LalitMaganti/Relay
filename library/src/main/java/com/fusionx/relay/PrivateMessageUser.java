package com.fusionx.relay;

import com.fusionx.relay.event.user.UserEvent;
import com.fusionx.relay.event.user.WorldPrivateActionEvent;
import com.fusionx.relay.event.user.WorldPrivateMessageEvent;
import com.fusionx.relay.interfaces.SubServerObject;
import com.fusionx.relay.util.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PrivateMessageUser extends User implements SubServerObject<UserEvent> {

    private final Server mServer;

    /**
     * Contains a copy of the messages when the conversation
     */
    private final List<UserEvent> mBuffer;

    /**
     * Retains whether the person on the other end of the PM quit in the middle of the conversation
     */
    private boolean mUserQuit;

    PrivateMessageUser(final String nick, final List<UserEvent> buffer) {
        super(nick, null);
        mBuffer = buffer;
        mServer = null;
    }

    public PrivateMessageUser(final String nick, final UserChannelInterface userChannelInterface,
            final String message, final boolean action) {
        super(nick, userChannelInterface);
        mBuffer = new ArrayList<>();
        mServer = userChannelInterface.getServer();

        if (Utils.isNotEmpty(message)) {
            final UserEvent event;
            if (action) {
                event = new WorldPrivateActionEvent(this, message);
            } else {
                event = new WorldPrivateMessageEvent(this, message);
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
        return mNick;
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
}
