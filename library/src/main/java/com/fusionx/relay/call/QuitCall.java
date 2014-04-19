package com.fusionx.relay.call;

import android.text.TextUtils;

public class QuitCall extends Call {

    private final String quitReason;

    public QuitCall(final String quitReason) {
        this.quitReason = quitReason;
    }

    @Override
    public String getLineToSendServer() {
        return TextUtils.isEmpty(quitReason) ? "QUIT" : "QUIT :" + quitReason;
    }
}