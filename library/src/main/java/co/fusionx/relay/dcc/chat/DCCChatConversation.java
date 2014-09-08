package co.fusionx.relay.dcc.chat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.dcc.event.chat.DCCChatEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatSelfActionEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatSelfMessageEvent;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.base.RelayAbstractConversation;
import co.fusionx.relay.misc.GenericBus;
import co.fusionx.relay.misc.RelayConfigurationProvider;

public class DCCChatConversation extends RelayAbstractConversation<DCCChatEvent> {

    private final ExecutorService mExecutorService;

    private final DCCChatConnection mDCCChatConnection;

    private final ServerConfiguration mServerConfiguration;

    private final DCCPendingConnection mPendingConnection;

    public DCCChatConversation(final GenericBus<Event> bus,
            final ServerConfiguration serverConfiguration,
            final DCCPendingConnection pendingConnection) {
        super(bus);

        mServerConfiguration = serverConfiguration;
        mPendingConnection = pendingConnection;

        mDCCChatConnection = new DCCChatConnection(mPendingConnection, this);
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void startChat() {
        mDCCChatConnection.startConnection();
    }

    public void sendMessage(final String message) {
        mExecutorService.submit(() -> mDCCChatConnection.writeLine(message));

        if (RelayConfigurationProvider.getPreferences().isSelfEventHidden()) {
            return;
        }
        getBus().post(new DCCChatSelfMessageEvent(this, null, message));
    }

    public void sendAction(final String action) {
        final String line = String.format("\u0001ACTION %1$s\u0001", action);
        mExecutorService.submit(() -> mDCCChatConnection.writeLine(line));

        if (RelayConfigurationProvider.getPreferences().isSelfEventHidden()) {
            return;
        }
        // TODO - this is wrong  fix it
        getBus().post(new DCCChatSelfActionEvent(this, null, action));
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
        return mServerConfiguration.getTitle().equals(that.mServerConfiguration.getTitle())
                && mPendingConnection.getDccRequestNick()
                .equals(that.mPendingConnection.getDccRequestNick());
    }

    @Override
    public int hashCode() {
        int result = mServerConfiguration.getTitle().hashCode();
        result = 31 * result + mPendingConnection.hashCode();
        return result;
    }
}