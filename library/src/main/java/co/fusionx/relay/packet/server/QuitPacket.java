package co.fusionx.relay.packet.server;

import android.text.TextUtils;

import co.fusionx.relay.packet.Packet;

public class QuitPacket implements Packet {

    private final String mQuitReason;

    public QuitPacket(final String quitReason) {
        mQuitReason = quitReason;
    }

    @Override
    public String getLine() {
        return TextUtils.isEmpty(mQuitReason) ? "QUIT" : String.format("QUIT :%s", mQuitReason);
    }
}