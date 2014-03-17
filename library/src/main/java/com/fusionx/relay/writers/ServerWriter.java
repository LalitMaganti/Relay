package com.fusionx.relay.writers;

import com.fusionx.relay.call.ChannelJoinCall;
import com.fusionx.relay.call.ModeCall;
import com.fusionx.relay.call.NickChangeCall;
import com.fusionx.relay.call.QuitCall;
import com.fusionx.relay.call.RawCall;
import com.fusionx.relay.call.VersionCall;
import com.fusionx.relay.call.WhoisCall;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.StringUtils;

import android.util.Base64;

import java.io.Writer;

public class ServerWriter extends RawWriter {

    public ServerWriter(final Writer out) {
        super(out);
    }

    public void sendUser(String userName, String realName) {
        writeLineToServer("USER " + userName + " 8 * :" + realName);
    }

    @Subscribe
    public void sendNick(final NickChangeCall nickChangeEvent) {
        writeLineToServer("NICK " + nickChangeEvent.newNick);
    }

    @Subscribe
    public void joinChannel(final ChannelJoinCall worldJoinEvent) {
        writeLineToServer("JOIN " + worldJoinEvent.channelName);
    }

    @Subscribe
    public void quitServer(final QuitCall quitEvent) {
        writeLineToServer(StringUtils.isEmpty(quitEvent.quitReason) ? "QUIT" : "QUIT :" + quitEvent
                .quitReason);
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
    public void sendChannelMode(final ModeCall event) {
        writeLineToServer("MODE " + event.channelName + " " + event.mode + " " + event.nick);
    }

    @Subscribe
    public void sendWhois(final WhoisCall event) {
        writeLineToServer("WHOIS " + event.nick);
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
    public void sendVersion(final VersionCall event) {
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
    public void sendRawLineToServer(final RawCall event) {
        writeLineToServer(event.rawLine);
    }
}