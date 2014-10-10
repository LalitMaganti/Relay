package co.fusionx.relay.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.fusionx.relay.core.InternalDCCChatConversation;
import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.core.SessionConfiguration;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.chat.DCCChatEvent;
import co.fusionx.relay.event.chat.DCCChatSelfActionEvent;
import co.fusionx.relay.event.chat.DCCChatSelfMessageEvent;
import co.fusionx.relay.internal.base.AbstractConversation;
import co.fusionx.relay.internal.core.Postable;

public class RelayDCCChatConversation extends AbstractConversation<DCCChatEvent>
        implements InternalDCCChatConversation {

    private final ExecutorService mExecutorService;

    private final RelayDCCChatConnection mRelayDCCChatConnection;

    private final SessionConfiguration mSessionConfiguration;

    private final RelayDCCPendingConnection mPendingConnection;

    private final LibraryUser mLibraryUser;

    public RelayDCCChatConversation(final Postable<Event> bus,
            final SessionConfiguration sessionConfiguration,
            final RelayDCCPendingConnection pendingConnection,
            final LibraryUser libraryUser) {
        super(bus);

        mSessionConfiguration = sessionConfiguration;
        mPendingConnection = pendingConnection;
        mLibraryUser = libraryUser;

        mRelayDCCChatConnection = new RelayDCCChatConnection(mPendingConnection, this);
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void startChat() {
        mRelayDCCChatConnection.startConnection();
    }

    @Override
    public void sendMessage(final String message) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                mRelayDCCChatConnection.writeLine(message);
            }
        });

        if (mSessionConfiguration.getSettingsProvider().isSelfEventHidden()) {
            return;
        }
        postEvent(new DCCChatSelfMessageEvent(this, mLibraryUser, message));
    }

    @Override
    public void sendAction(final String action) {
        final String line = String.format("\u0001ACTION %1$s\u0001", action);
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                mRelayDCCChatConnection.writeLine(line);
            }
        });

        if (mSessionConfiguration.getSettingsProvider().isSelfEventHidden()) {
            return;
        }
        postEvent(new DCCChatSelfActionEvent(this, mLibraryUser, action));
    }

    @Override
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
        } else if (!(o instanceof RelayDCCChatConversation)) {
            return false;
        }

        final RelayDCCChatConversation that = (RelayDCCChatConversation) o;
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