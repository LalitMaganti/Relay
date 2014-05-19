package com.fusionx.relay.writers;

import com.fusionx.relay.call.ChannelJoinCall;
import com.fusionx.relay.call.ModeCall;
import com.fusionx.relay.call.NickChangeCall;
import com.fusionx.relay.call.QuitCall;
import com.fusionx.relay.call.RawCall;
import com.fusionx.relay.call.UserCall;
import com.fusionx.relay.call.VersionResponseCall;
import com.fusionx.relay.call.WhoisCall;
import com.squareup.otto.Subscribe;

import android.util.Base64;

import java.io.Writer;

public class ServerWriter extends RawWriter {

    public ServerWriter(final Writer out) {
        super(out);
    }

    @Subscribe
    public void sendUser(final UserCall userCall) {
        writeLineToServer(userCall.getLineToSendServer());
    }

    @Subscribe
    public void sendNick(final NickChangeCall nickChangeEvent) {
        writeLineToServer(nickChangeEvent.getLineToSendServer());
    }

    @Subscribe
    public void joinChannel(final ChannelJoinCall worldJoinEvent) {
        writeLineToServer(worldJoinEvent.getLineToSendServer());
    }

    @Subscribe
    public void quitServer(final QuitCall quitEvent) {
        writeLineToServer(quitEvent.getLineToSendServer());
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

    public void sendSupportedCAP() {
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
    public void sendVersion(final VersionResponseCall event) {
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