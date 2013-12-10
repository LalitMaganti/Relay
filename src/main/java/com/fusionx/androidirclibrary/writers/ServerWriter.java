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

import com.fusionx.androidirclibrary.event.Event;
import com.fusionx.androidirclibrary.event.JoinEvent;
import com.fusionx.androidirclibrary.event.ModeEvent;
import com.fusionx.androidirclibrary.event.NickChangeEvent;
import com.fusionx.androidirclibrary.event.QuitEvent;
import com.fusionx.androidirclibrary.event.VersionEvent;
import com.fusionx.androidirclibrary.event.WhoisEvent;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import android.util.Base64;

import java.io.OutputStreamWriter;

public class ServerWriter extends RawWriter {

    public ServerWriter(final OutputStreamWriter out) {
        super(out);
    }

    public void sendUser(String userName, String realName) {
        writeLineToServer("USER " + userName + " 8 * :" + realName);
    }

    @Subscribe
    public void changeNick(final NickChangeEvent nickChangeEvent) {
        writeLineToServer("NICK " + nickChangeEvent.newNick);
    }

    @Subscribe
    public void joinChannel(final JoinEvent joinEvent) {
        writeLineToServer("JOIN " + joinEvent.channelToJoin);
    }

    @Subscribe
    public void quitServer(final QuitEvent quitEvent) {
        writeLineToServer(StringUtils.isEmpty(quitEvent.reason) ? "QUIT" : "QUIT :" + quitEvent
                .reason);
    }

    public void pongServer(final String absoluteURL) {
        writeLineToServer("PONG " + absoluteURL);
    }

    public void sendServerPassword(final String password) {
        writeLineToServer("PASS " + password);
    }

    public void sendNickServPassword(final String password) {
        writeLineToServer("NICKSERV IDENTIFY " + password);
    }

    @Subscribe
    public void sendChannelMode(final ModeEvent event) {
        writeLineToServer("MODE " + event.channel + " " + event.mode + " " + event.nick);
    }

    @Subscribe
    public void sendWhois(final WhoisEvent event) {
        writeLineToServer("WHOIS " + event.baseMessage);
    }

    public void getSupportedCapabilities() {
        writeLineToServer("CAP LS");
    }

    public void sendEndCap() {
        writeLineToServer("CAP END");
    }

    public void requestSasl() {
        writeLineToServer("CAP REQ : sasl multi-prefix");
    }

    public void sendPlainSaslAuthentication() {
        writeLineToServer("AUTHENTICATE PLAIN");
    }

    @Subscribe
    public void sendVersion(final VersionEvent event) {
        writeLineToServer("PRIVMSG " + event.askingUser + " :\u0001VERSION " + event.version +
                "\u0001");
    }

    public void sendSaslAuthentication(final String saslUsername, final String saslPassword) {
        final String authentication = saslUsername + "\0" + saslUsername + "\0" + saslPassword;
        final String encoded = Base64.encodeToString(authentication.getBytes(), Base64.DEFAULT);
        writeLineToServer("AUTHENTICATE " + encoded);
    }

    /**
     * This is a very advanced feature that should only be called when the user specifically
     * requests it using a /raw command or is a / message that we don't know how to parse
     *
     * @param event - the event with the raw line that you want to send
     */
    @Subscribe
    public void sendRawLineToServer(final Event event) {
        writeLineToServer(event.baseMessage);
    }
}