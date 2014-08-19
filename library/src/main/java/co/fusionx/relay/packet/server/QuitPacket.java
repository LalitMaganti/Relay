package co.fusionx.relay.packet.server;

import android.text.TextUtils;

import co.fusionx.relay.packet.Packet;

public class QuitPacket implements Packet {

    private final String quitReason;

    public QuitPacket(final String quitReason) {
        this.quitReason = quitReason;
    }

    @Override
    public String getLineToSendServer() {
        return TextUtils.isEmpty(quitReason) ? "QUIT" : "QUIT :" + quitReason;
    }
}