package co.fusionx.relay.internal.packet.server.cap;

import com.google.common.io.BaseEncoding;

import android.util.Base64;

import co.fusionx.relay.internal.packet.Packet;

public class CAPPlainSASLAuthPacket implements Packet {

    public final String mSaslUsername;

    public final String mSaslPassword;

    public CAPPlainSASLAuthPacket(final String saslUsername, final String saslPassword) {
        mSaslUsername = saslUsername;
        mSaslPassword = saslPassword;
    }

    @Override
    public String getLine() {
        final String authentication = mSaslUsername + "\0" + mSaslUsername + "\0" + mSaslPassword;
        final String encoded = BaseEncoding.base64().encode(authentication.getBytes());
        return String.format("AUTHENTICATE %s", encoded);
    }
}
