package com.fusionx.relay.call.server;

import com.fusionx.relay.call.Call;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeResponseCall extends Call {

    private final String mRecipient;

    public TimeResponseCall(String nick) {
        mRecipient = nick;
    }

    @Override
    public String getLineToSendServer() {
        final Date date = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return String.format("NOTICE %s \u0001TIME :%s\u0001", mRecipient,
                dateFormat.format(date));
    }
}
