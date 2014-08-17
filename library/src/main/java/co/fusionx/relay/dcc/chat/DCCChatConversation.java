package co.fusionx.relay.dcc.chat;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.dcc.DCCConversation;
import co.fusionx.relay.dcc.event.chat.DCCChatEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatSelfMessageEvent;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.misc.RelayConfigurationProvider;

public class DCCChatConversation extends DCCConversation {

    private final Handler mCallHandler;

    private final DCCChatConnection mDCCChatConnection;

    private final DCCPendingConnection mPendingConnection;

    private List<DCCChatEvent> mBuffer;

    public DCCChatConversation(final RelayServer server,
            final DCCPendingConnection pendingConnection) {
        super(server);
        mPendingConnection = pendingConnection;

        mDCCChatConnection = new DCCChatConnection(mPendingConnection, this);

        mBuffer = new ArrayList<>();

        final HandlerThread handlerThread = new HandlerThread("dccConnection");
        handlerThread.start();
        mCallHandler = new Handler(handlerThread.getLooper());
    }

    public List<DCCChatEvent> getBuffer() {
        return mBuffer;
    }

    void postAndStoreEvent(final DCCChatEvent event) {
        mBuffer.add(event);
        mServer.getServerEventBus().post(event);
    }

    public void sendMessage(final String message) {
        mCallHandler.post(() -> mDCCChatConnection.writeLine(message));

        if (RelayConfigurationProvider.getPreferences().isSelfEventHidden()) {
            return;
        }
        postAndStoreEvent(new DCCChatSelfMessageEvent(this, message));
    }

    public void startChat() {
        mDCCChatConnection.startConnection();
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
        return mPendingConnection.getDccRequestNick()
                .equals(that.mPendingConnection.getDccRequestNick())
                && mServer.equals(that.mServer);
    }

    @Override
    public int hashCode() {
        int result = mServer.hashCode();
        result = 31 * result + mPendingConnection.hashCode();
        return result;
    }
}