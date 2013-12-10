package com.fusionx.relay.writers;

import com.fusionx.relay.event.Event;
import com.fusionx.relay.event.JoinEvent;
import com.fusionx.relay.event.ModeEvent;
import com.fusionx.relay.event.NickChangeEvent;
import com.fusionx.relay.event.QuitEvent;
import com.fusionx.relay.event.VersionEvent;
import com.fusionx.relay.event.WhoisEvent;
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