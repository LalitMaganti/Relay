/*
    HoloIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of HoloIRC.

    HoloIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HoloIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HoloIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.androidirclibrary;

import com.fusionx.androidirclibrary.communication.ServerReceiverBus;
import com.fusionx.androidirclibrary.communication.ServerSenderBus;
import com.fusionx.androidirclibrary.connection.ServerConnection;
import com.fusionx.androidirclibrary.event.Event;
import com.fusionx.androidirclibrary.event.ServerEvent;
import com.fusionx.androidirclibrary.misc.InterfaceHolders;
import com.fusionx.androidirclibrary.misc.ServerCache;
import com.fusionx.androidirclibrary.util.IRCUtils;
import com.fusionx.androidirclibrary.writers.ChannelWriter;
import com.fusionx.androidirclibrary.writers.ServerWriter;
import com.fusionx.androidirclibrary.writers.UserWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {

    private final String mTitle;

    private final UserChannelInterface mUserChannelInterface;

    private AppUser mUser;

    private final List<Message> mBuffer;

    private String mStatus;

    private final ServerCache mServerCache;

    private final ServerSenderBus mServerSenderBus;

    private final ServerReceiverBus mServerReceiverBus;

    public Server(final String serverTitle, final ServerConnection connection) {
        mTitle = serverTitle;
        mBuffer = new ArrayList<Message>();
        mStatus = "Disconnected";
        mServerCache = new ServerCache();
        mServerSenderBus = new ServerSenderBus();
        mServerReceiverBus = new ServerReceiverBus(connection);
        mUserChannelInterface = new UserChannelInterface(this);
    }

    public void onServerEvent(final ServerEvent event) {
        if (StringUtils.isNotBlank(event.message)) {
            synchronized (mBuffer) {
                mBuffer.add(new Message(event.message));
            }
        }
    }

    public Event onPrivateMessage(final PrivateMessageUser userWhoIsNotUs,
            final String message, final boolean weAreSending) {
        final User sendingUser = weAreSending ? mUser : userWhoIsNotUs;
        final boolean doesPrivateMessageExist = mUser.isPrivateMessageOpen(userWhoIsNotUs);
        if (!doesPrivateMessageExist) {
            mUser.createPrivateMessage(userWhoIsNotUs);
        }
        if (!weAreSending || InterfaceHolders.getPreferences().shouldSendSelfMessageEvent()) {
            return mServerSenderBus.sendPrivateMessage(userWhoIsNotUs, sendingUser, message,
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
        if (!weAreSending || InterfaceHolders.getPreferences().shouldSendSelfMessageEvent()) {
            return mServerSenderBus.sendPrivateAction(userWhoIsNotUs, sendingUser, action,
                    !doesPrivateMessageExist);
        } else {
            return new Event("");
        }
    }

    public synchronized PrivateMessageUser getPrivateMessageUser(final String nick) {
        final Iterator<PrivateMessageUser> iterator = mUser.getPrivateMessageIterator();
        while (iterator.hasNext()) {
            final PrivateMessageUser privateMessageUser = iterator.next();
            if (IRCUtils.areNicksEqual(privateMessageUser.getNick(), nick)) {
                return privateMessageUser;
            }
        }
        return new PrivateMessageUser(nick, mUserChannelInterface);
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
     * @param writer - the which the writers will use
     * @return  - the server writer created from the OutputStreamWriter
     */
    public ServerWriter onOutputStreamCreated(final OutputStreamWriter writer) {
        final ServerWriter serverWriter = new ServerWriter(writer);
        mServerReceiverBus.register(serverWriter);
        mServerReceiverBus.register(new ChannelWriter(writer));
        mServerReceiverBus.register(new UserWriter(writer));
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

    public ServerReceiverBus getServerReceiverBus() {
        return mServerReceiverBus;
    }

    public ServerSenderBus getServerSenderBus() {
        return mServerSenderBus;
    }
}