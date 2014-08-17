package co.fusionx.relay.bus;

import android.os.Handler;
import android.util.Base64;

import java.io.BufferedWriter;
import java.io.IOException;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.call.Call;
import co.fusionx.relay.call.server.ERRMSGResponseCall;
import co.fusionx.relay.call.server.FingerResponseCall;
import co.fusionx.relay.call.server.PingResponseCall;
import co.fusionx.relay.call.server.TimeResponseCall;
import co.fusionx.relay.call.server.UserCall;
import co.fusionx.relay.call.server.VersionResponseCall;
import co.fusionx.relay.misc.RelayConfigurationProvider;

public class ServerCallHandler {

    private final Handler mCallHandler;

    private BufferedWriter mBufferedWriter;

    public ServerCallHandler(final Handler callHandler) {
        mCallHandler = callHandler;
    }

    void writeLineToServer(final String line) {
        if (mBufferedWriter == null) {
            RelayConfigurationProvider.getPreferences().logServerLine(line);
            return;
        }

        try {
            mBufferedWriter.write(line + "\r\n");
            mBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void post(final Call call) {
        mCallHandler.post(() -> postImmediately(call));
    }

    public void postImmediately(final Call call) {
        writeLineToServer(call.getLineToSendServer());
    }

    public void onOutputStreamCreated(final BufferedWriter writer) {
        mBufferedWriter = writer;
    }

    public void onConnectionTerminated() {
        mBufferedWriter = null;
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

    public void sendSaslAuthentication(final String saslUsername, final String saslPassword) {
        final String authentication = saslUsername + "\0" + saslUsername + "\0" + saslPassword;
        final String encoded = Base64.encodeToString(authentication.getBytes(), Base64.DEFAULT);
        writeLineToServer("AUTHENTICATE " + encoded);
    }

    public void sendUser(final String serverUserName, final String realName) {
        post(new UserCall(serverUserName, realName));
    }

    public void sendFingerResponse(final String nick, final RelayServer server) {
        post(new FingerResponseCall(nick, server));
    }

    public void sendVersionResponse(final String nick) {
        post(new VersionResponseCall(nick));
    }

    public void sendErrMsgResponse(final String nick, final String query) {
        post(new ERRMSGResponseCall(nick, query));
    }

    public void sendPingResponse(final String nick, final String timestamp) {
        post(new PingResponseCall(nick, timestamp));
    }

    public void sendTimeResponse(final String nick) {
        post(new TimeResponseCall(nick));
    }
}