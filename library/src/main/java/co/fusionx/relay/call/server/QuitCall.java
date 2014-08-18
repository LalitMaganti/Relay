package co.fusionx.relay.call.server;

import android.text.TextUtils;

import co.fusionx.relay.call.Call;

public class QuitCall implements Call {

    private final String quitReason;

    public QuitCall(final String quitReason) {
        this.quitReason = quitReason;
    }

    @Override
    public String getLineToSendServer() {
        return TextUtils.isEmpty(quitReason) ? "QUIT" : "QUIT :" + quitReason;
    }
}