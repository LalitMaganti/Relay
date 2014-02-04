package com.fusionx.relay;

import com.fusionx.relay.event.user.UserEvent;
import com.fusionx.relay.event.user.WorldPrivateActionEvent;
import com.fusionx.relay.event.user.WorldPrivateMessageEvent;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PrivateMessageUser extends User {

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
    }

    public PrivateMessageUser(final String nick, final UserChannelInterface userChannelInterface,
            final String message, final boolean action) {
        super(nick, userChannelInterface);
        mBuffer = new ArrayList<>();

        if (StringUtils.isNotEmpty(message)) {
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
    public List<UserEvent> getBuffer() {
        return mBuffer;
    }

    public boolean isUserQuit() {
        return mUserQuit;
    }

    public void setUserQuit(boolean userQuit) {
        mUserQuit = userQuit;
    }
}
