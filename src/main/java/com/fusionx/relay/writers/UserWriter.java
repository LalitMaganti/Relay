package com.fusionx.relay.writers;

import com.fusionx.relay.event.PrivateActionEvent;
import com.fusionx.relay.event.PrivateMessageEvent;
import com.squareup.otto.Subscribe;

import java.io.OutputStreamWriter;

public class UserWriter extends RawWriter {

    public UserWriter(OutputStreamWriter writer) {
        super(writer);
    }

    @Subscribe
    public void sendMessage(final PrivateMessageEvent event) {
        writeLineToServer(String.format(WriterCommands.PRIVMSG, event.userNick, event.message));
    }

    @Subscribe
    public void sendAction(final PrivateActionEvent event) {
        writeLineToServer(String.format(WriterCommands.Action, event.userNick, event.message));
    }
}