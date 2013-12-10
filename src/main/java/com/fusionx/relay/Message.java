package com.fusionx.relay;

import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.ColourParserUtils;

import android.text.Spanned;
import android.text.format.Time;

public class Message {

    public final String timestamp;

    public final Spanned message;

    public Message(final String message) {
        if (InterfaceHolders.getPreferences().getShouldTimestampMessages()) {
            final Time now = new Time();
            now.setToNow();
            this.timestamp = now.format("%H:%M");
        } else {
            this.timestamp = "";
        }
        this.message = ColourParserUtils.parseMarkup(message);
    }
}
