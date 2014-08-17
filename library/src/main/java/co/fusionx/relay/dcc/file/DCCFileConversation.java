package co.fusionx.relay.dcc.file;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.dcc.DCCConnection;
import co.fusionx.relay.dcc.DCCConversation;
import co.fusionx.relay.dcc.pending.DCCPendingSendConnection;

public class DCCFileConversation extends DCCConversation {

    private final String mNick;

    private final List<DCCFileConnection> mConnectionList;

    public DCCFileConversation(final RelayServer server, final String nick) {
        super(server);

        mNick = nick;
        mConnectionList = new ArrayList<>();
    }

    public void getFile(final DCCPendingSendConnection connection, final File file) {
        final DCCGetConnection getConnection = new DCCGetConnection(connection, this, file);
        mConnectionList.add(getConnection);
        getConnection.startConnection();

        mServer.getServerEventBus().post(new DCCGetStartedEvent(getConnection));
    }

    public Collection<DCCFileConnection> getFileConnections() {
        return ImmutableList.copyOf(mConnectionList);
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