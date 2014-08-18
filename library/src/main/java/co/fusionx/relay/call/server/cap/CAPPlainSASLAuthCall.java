package co.fusionx.relay.call.server.cap;

import android.util.Base64;

import co.fusionx.relay.call.Call;

public class CAPPlainSASLAuthCall implements Call {

    public final String mSaslUsername;

    public final String mSaslPassword;

    public CAPPlainSASLAuthCall(final String saslUsername, final String saslPassword) {
        mSaslUsername = saslUsername;
        mSaslPassword = saslPassword;
    }

    @Override
    public String getLineToSendServer() {
        final String authentication = mSaslUsername + "\0" + mSaslUsername + "\0" + mSaslPassword;
        final String encoded = Base64.encodeToString(authentication.getBytes(), Base64.DEFAULT);
        return "AUTHENTICATE " + encoded;
    }
}
