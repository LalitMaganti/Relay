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

import com.fusionx.androidirclibrary.communication.MessageSender;
import com.fusionx.androidirclibrary.connection.ServerConnection;
import com.fusionx.androidirclibrary.event.Event;
import com.fusionx.androidirclibrary.event.ServerEvent;
import com.fusionx.androidirclibrary.misc.InterfaceHolders;
import com.fusionx.androidirclibrary.misc.ServerCache;
import com.fusionx.androidirclibrary.util.IRCUtils;
import com.fusionx.androidirclibrary.writers.ServerWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {

    private final String mTitle;

    private final ServerConnection mWrapper;

    private ServerWriter mWriter;

    private UserChannelInterface mUserChannelInterface;

    private AppUser mUser;

    private final List<Message> mBuffer;

    private String mStatus;

    private boolean mCached;

    private final ServerCache mServerCache;

    public Server(final String serverTitle, final ServerConnection wrapper) {
        mTitle = serverTitle;
        mWrapper = wrapper;
        mBuffer = new ArrayList<Message>();
        mStatus = "Disconnected";
        mServerCache = new ServerCache();
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
        final MessageSender sender = MessageSender.getSender(mTitle);
        final User sendingUser = weAreSending ? mUser : userWhoIsNotUs;
        if (!mUser.isPrivateMessageOpen(userWhoIsNotUs)) {
            mUser.createPrivateMessage(userWhoIsNotUs);

            if (StringUtils.isNotEmpty(message)) {
                sender.sendPrivateMessage(userWhoIsNotUs, sendingUser, message);
            }

            return sender.sendNewPrivateMessage(userWhoIsNotUs.getNick());
        } else {
            if (StringUtils.isNotEmpty(message)) {
                return sender.sendPrivateMessage(userWhoIsNotUs, sendingUser, message);
            } else {
                return new Event(userWhoIsNotUs.getNick());
            }
        }
    }

    public Event onPrivateAction(final PrivateMessageUser userWhoIsNotUs, final String action,
            final boolean weAreSending) {
        final MessageSender sender = MessageSender.getSender(mTitle);
        final User sendingUser = weAreSending ? mUser : userWhoIsNotUs;
        if (!mUser.isPrivateMessageOpen(userWhoIsNotUs)) {
            mUser.createPrivateMessage(userWhoIsNotUs);

            if (StringUtils.isNotEmpty(action)) {
                sender.sendPrivateAction(userWhoIsNotUs, sendingUser, action);
            }

            return sender.sendNewPrivateMessage(userWhoIsNotUs.getNick());
        } else {
            if (StringUtils.isNotEmpty(action)) {
                return sender.sendPrivateAction(userWhoIsNotUs, sendingUser, action);
            } else {
                return new Event(userWhoIsNotUs.getNick());
            }
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
        //context.getString(R.string.status_connected));
    }

    public void disconnectFromServer() {
        mWrapper.disconnectFromServer();
    }

    public void cleanup() {
        mWriter = null;
        mUserChannelInterface = null;
        mUser = null;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Server) && ((Server) o).getTitle().equals(mTitle);
    }

    public void setupUserChannelInterface(final OutputStreamWriter streamWriter) {
        mUserChannelInterface = new UserChannelInterface(streamWriter, this);
    }

    // Getters and Setters
    public List<Message> getBuffer() {
        return mBuffer;
    }

    public ServerWriter getWriter() {
        return mWriter;
    }

    public void setWriter(final ServerWriter writer) {
        mWriter = writer;
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

    public String getTitle() {
        return mTitle;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(final String status) {
        mStatus = status;
    }

    public boolean isCached() {
        return mCached;
    }

    public void setCached(final boolean cached) {
        mCached = cached;
    }

    public ServerCache getServerCache() {
        return mServerCache;
    }
}