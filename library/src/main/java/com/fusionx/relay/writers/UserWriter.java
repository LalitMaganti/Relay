package com.fusionx.relay.writers;

import com.fusionx.relay.call.user.PrivateActionCall;
import com.fusionx.relay.call.user.PrivateMessageCall;
import com.squareup.otto.Subscribe;

import java.io.Writer;

public class UserWriter extends RawWriter {

    public UserWriter(final Writer writer) {
        super(writer);
    }

    @Subscribe
    public void sendMessage(final PrivateMessageCall event) {
        writeLineToServer(String.format(WriterCommands.PRIVMSG, event.userNick, event.message));
    }

    @Subscribe
    public void sendAction(final PrivateActionCall event) {
        writeLineToServer(String.format(WriterCommands.ACTION, event.userNick, event.message));
    }
}