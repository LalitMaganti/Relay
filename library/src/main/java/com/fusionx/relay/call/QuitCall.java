package com.fusionx.relay.call;

public class QuitCall extends Call {

    public final String quitReason;

    public QuitCall(final String quitReason) {
        this.quitReason = quitReason;
    }
}