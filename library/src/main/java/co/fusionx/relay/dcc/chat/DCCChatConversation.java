package co.fusionx.relay.dcc.chat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.fusionx.relay.internal.base.AbstractConversation;
import co.fusionx.relay.internal.bus.PostableBus;
import co.fusionx.relay.core.SessionConfiguration;
import co.fusionx.relay.dcc.event.chat.DCCChatEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatSelfActionEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatSelfMessageEvent;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.event.Event;

public class DCCChatConversation extends AbstractConversation<DCCChatEvent> {

    private final ExecutorService mExecutorService;

    private final DCCChatConnection mDCCChatConnection;

    private final SessionConfiguration mSessionConfiguration;

    private final DCCPendingConnection mPendingConnection;

    public DCCChatConversation(final PostableBus<Event> bus,
            final SessionConfiguration sessionConfiguration,
            final DCCPendingConnection pendingConnection) {
        super(bus);

        mSessionConfiguration = sessionConfiguration;
        mPendingConnection = pendingConnection;

        mDCCChatConnection = new DCCChatConnection(mPendingConnection, this);
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void startChat() {
        mDCCChatConnection.startConnection();
    }

    public void sendMessage(final String message) {
        mExecutorService.submit(() -> mDCCChatConnection.writeLine(message));

        if (mSessionConfiguration.getSettingsProvider().isSelfEventHidden()) {
            return;
        }
        postEvent(new DCCChatSelfMessageEvent(this, null, message));
    }

    public void sendAction(final String action) {
        final String line = String.format("\u0001ACTION %1$s\u0001", action);
        mExecutorService.submit(() -> mDCCChatConnection.writeLine(line));

        if (mSessionConfiguration.getSettingsProvider().isSelfEventHidden()) {
            return;
        }
        // TODO - this is wrong  fix it
        postEvent(new DCCChatSelfActionEvent(this, null, action));
    }

    public void closeChat() {

    }

    // Conversation interface
    @Override
    public String getId() {
        return mPendingConnection.getDccRequestNick();
    }

    // Equality
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof DCCChatConversation)) {
            return false;
        }

        final DCCChatConversation that = (DCCChatConversation) o;
        return mSessionConfiguration.getConnectionConfiguration().getTitle()
                .equals(that.mSessionConfiguration.getConnectionConfiguration().getTitle())
                && mPendingConnection.getDccRequestNick()
                .equals(that.mPendingConnection.getDccRequestNick());
    }

    @Override
    public int hashCode() {
        int result = mSessionConfiguration.getConnectionConfiguration().getTitle().hashCode();
        result = 31 * result + mPendingConnection.hashCode();
        return result;
    }
}