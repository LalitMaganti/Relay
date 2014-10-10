package co.fusionx.relay.internal.packet.server;

import co.fusionx.relay.internal.packet.Packet;

public class UserPacket implements Packet {

    private final String mUserName;

    private final String mRealName;

    public UserPacket(final String userName, final String realName) {
        mUserName = userName;
        mRealName = realName;
    }

    @Override
    public String getLine() {
        return String.format("USER %s 8 * :%s", mUserName, mRealName);
    }
}