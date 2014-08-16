package co.fusionx.relay.bus;

import android.os.Handler;
import android.util.Base64;

import java.io.BufferedWriter;
import java.io.IOException;

import co.fusionx.relay.call.Call;

public class ServerCallHandler {

    private final Handler mCallHandler;

    private BufferedWriter mBufferedWriter;

    public ServerCallHandler(final Handler callHandler) {
        mCallHandler = callHandler;
    }

    void writeLineToServer(final String line) {
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
}