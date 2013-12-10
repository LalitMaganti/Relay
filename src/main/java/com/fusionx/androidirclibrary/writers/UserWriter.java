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

package com.fusionx.androidirclibrary.writers;

import com.fusionx.androidirclibrary.event.PrivateActionEvent;
import com.fusionx.androidirclibrary.event.PrivateMessageEvent;
import com.squareup.otto.Subscribe;

import java.io.OutputStreamWriter;

public class UserWriter extends RawWriter {
    public UserWriter(OutputStreamWriter writer) {
        super(writer);
    }

    @Subscribe
    public void sendMessage(final PrivateMessageEvent event) {
        writeLineToServer(String.format(WriterCommands.PRIVMSG, event.userNick, event.message));
    }

    @Subscribe
    public void sendAction(final PrivateActionEvent event) {
        writeLineToServer(String.format(WriterCommands.Action, event.userNick, event.message));
    }
}