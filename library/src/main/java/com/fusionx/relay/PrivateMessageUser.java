package com.fusionx.relay;

import com.fusionx.relay.event.user.UserEvent;
import com.fusionx.relay.event.user.WorldPrivateActionEvent;
import com.fusionx.relay.event.user.WorldPrivateMessageEvent;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class PrivateMessageUser extends User {

    /**
     * Contains a copy of the messages when the conversation
     */
    private final List<UserEvent> mBuffer = new ArrayList<>();

    /**
     * Retains whether the person on the other end of the PM quit in the middle of the conversation
     */
    private boolean mUserQuit;

    public PrivateMessageUser(final String nick, final UserChannelInterface userChannelInterface,
            final String message, final boolean action) {
        super(nick, userChannelInterface);

        if (StringUtils.isNotEmpty(message)) {
            final UserEvent event;
            if (action) {
                event = new WorldPrivateActionEvent(this, message);
            } else {
                event = new WorldPrivateMessageEvent(this, message);
            }
            mBuffer.add(event);
        }

        /*if (InterfaceHolders.getPreferences().shouldHandleInitialPrivateMessage() && Utils
                .isNotEmpty(initalMessage)) {*/
        //}
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
