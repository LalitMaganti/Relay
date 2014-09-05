package co.fusionx.relay.dcc.file;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.base.relay.RelayAbstractConversation;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.dcc.event.file.DCCFileEvent;
import co.fusionx.relay.dcc.event.file.DCCFileGetStartedEvent;
import co.fusionx.relay.dcc.pending.DCCPendingSendConnection;

public class DCCFileConversation extends RelayAbstractConversation<DCCFileEvent> {

    private final String mNick;

    private final Map<String, DCCFileConnection> mConnectionList;

    public DCCFileConversation(final RelayServer server, final String nick) {
        super(server);

        mNick = nick;

        mConnectionList = new HashMap<>();
    }

    public DCCFileConnection getFileConnection(final String fileName) {
        return mConnectionList.get(fileName);
    }

    public void getFile(final DCCPendingSendConnection connection, final File file) {
        final DCCGetConnection getConnection = new DCCGetConnection(connection, this, file);
        mConnectionList.put(connection.getArgument(), getConnection);
        getConnection.startConnection();

        postAndStoreEvent(new DCCFileGetStartedEvent(this, getConnection));
    }

    public Collection<DCCFileConnection> getFileConnections() {
        return ImmutableList.copyOf(mConnectionList.values());
    }

    // Conversation interface
    @Override
    public String getId() {
        return mNick;
    }

    // Equality
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof DCCFileConversation)) {
            return false;
        }

        final DCCFileConversation that = (DCCFileConversation) o;
        return mNick.equals(that.mNick) && mServer.equals(that.mServer);
    }

    @Override
    public int hashCode() {
        int result = mServer.hashCode();
        result = 31 * result + mNick.hashCode();
        return result;
    }
}