package com.fusionx.relay;

import com.fusionx.relay.communication.ServerCallBus;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.connection.ServerConnection;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.event.ServerEvent;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.misc.ServerCache;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.writers.ChannelWriter;
import com.fusionx.relay.writers.ServerWriter;
import com.fusionx.relay.writers.UserWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {

    private final String mTitle;

    private final UserChannelInterface mUserChannelInterface;

    private final List<Message> mBuffer;

    private final ServerCache mServerCache;

    private final ServerEventBus mServerEventBus;

    private final ServerCallBus mServerCallBus;

    private AppUser mUser;

    private String mStatus;

    public Server(final String serverTitle, final ServerConnection connection) {
        mTitle = serverTitle;
        mBuffer = new ArrayList<Message>();
        mStatus = "Disconnected";
        mServerCache = new ServerCache();
        mServerEventBus = new ServerEventBus(serverTitle);
        mServerCallBus = new ServerCallBus(connection);
        mUserChannelInterface = new UserChannelInterface(this);
    }

    public void onServerEvent(final ServerEvent event) {
        if (StringUtils.isNotBlank(event.message)) {
            synchronized (mBuffer) {
                mBuffer.add(new Message(event.message));
            }
        }
    }

    public Event onPrivateMessage(final PrivateMessageUser userWhoIsNotUs, final String message,
            final boolean weAreSending) {
        final User sendingUser = weAreSending ? mUser : userWhoIsNotUs;
        final boolean doesPrivateMessageExist = mUser.isPrivateMessageOpen(userWhoIsNotUs);
        if (!doesPrivateMessageExist) {
            mUser.createPrivateMessage(userWhoIsNotUs);
        }
        if (!weAreSending || InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            return mServerEventBus.onPrivateMessage(userWhoIsNotUs, sendingUser, message,
                    !doesPrivateMessageExist);
        } else {
            return new Event("");
        }
    }

    public Event onPrivateAction(final PrivateMessageUser userWhoIsNotUs, final String action,
            final boolean weAreSending) {
        final User sendingUser = weAreSending ? mUser : userWhoIsNotUs;
        final boolean doesPrivateMessageExist = mUser.isPrivateMessageOpen(userWhoIsNotUs);
        if (!doesPrivateMessageExist) {
            mUser.createPrivateMessage(userWhoIsNotUs);
        }
        if (!weAreSending || InterfaceHolders.getPreferences().isSelfEventBroadcast()) {
            return mServerEventBus.onPrivateAction(userWhoIsNotUs, sendingUser, action,
                    !doesPrivateMessageExist);
        } else {
            return new Event("");
        }
    }

    public synchronized PrivateMessageUser getPrivateMessageUserIfExists(final String nick) {
        final Iterator<PrivateMessageUser> iterator = mUser.getPrivateMessageIterator();
        while (iterator.hasNext()) {
            final PrivateMessageUser privateMessageUser = iterator.next();
            if (IRCUtils.areNicksEqual(privateMessageUser.getNick(), nick)) {
                return privateMessageUser;
            }
        }
        return null;
    }

    public synchronized PrivateMessageUser getPrivateMessageUser(final String nick,
            final String initialMessage) {
        final PrivateMessageUser user = getPrivateMessageUserIfExists(nick);
        if (user == null) {
            return new PrivateMessageUser(nick, mUserChannelInterface, initialMessage);
        } else {
            return user;
        }
    }

    public boolean isConnected() {
        return mStatus.equals(InterfaceHolders.getEventResponses().getConnectedStatus());
    }

    public void onCleanup() {
        mUserChannelInterface.onCleanup();
        mUser = null;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Server) && ((Server) o).getTitle().equals(mTitle);
    }

    /**
     * Sets up the writers based on the output stream passed into the method
     *
     * @param writer the which the writers will use
     * @return the server writer created from the OutputStreamWriter
     */
    public ServerWriter onOutputStreamCreated(final OutputStreamWriter writer) {
        final ServerWriter serverWriter = new ServerWriter(writer);
        mServerCallBus.register(serverWriter);
        mServerCallBus.register(new ChannelWriter(writer));
        mServerCallBus.register(new UserWriter(writer));
        return serverWriter;
    }

    // Getters and Setters
    public List<Message> getBuffer() {
        return mBuffer;
    }

    public UserChannelInterface getUserChannelInterface() {
        return mUserChannelInterface;
    }

    public AppUser getUser() {
        return mUser;
    }

    public void setUser(final AppUser user) {
        mUser = user;
    }

    String getTitle() {
        return mTitle;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(final String status) {
        mStatus = status;
    }

    public ServerCache getServerCache() {
        return mServerCache;
    }

    public ServerCallBus getServerCallBus() {
        return mServerCallBus;
    }

    public ServerEventBus getServerEventBus() {
        return mServerEventBus;
    }
}