package co.fusionx.relay.packet.server;

import co.fusionx.relay.packet.Packet;

public class UserPacket implements Packet {

    private final String mUserName;

    private final String mRealName;

    public UserPacket(final String userName, final String realName) {
        mUserName = userName;
        mRealName = realName;
    }

    @Override
    public String getLineToSendServer() {
        return String.format("USER %s 8 * :%s", mUserName, mRealName);
    }
}