package co.fusionx.relay.dcc.file;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.dcc.event.file.DCCFileEvent;
import co.fusionx.relay.dcc.event.file.DCCFileGetStartedEvent;
import co.fusionx.relay.dcc.pending.DCCPendingSendConnection;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.base.RelayAbstractConversation;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.misc.GenericBus;

public class DCCFileConversation extends RelayAbstractConversation<DCCFileEvent> {

    private final ServerConfiguration mServerConfiguration;

    private final BaseSender mBaseSender;

    private final String mNick;

    private final Map<String, DCCFileConnection> mConnectionList;

    public DCCFileConversation(final GenericBus<Event> bus,
            final ServerConfiguration serverConfiguration,
            final BaseSender baseSender, final String nick) {
        super(bus);

        mServerConfiguration = serverConfiguration;
        mBaseSender = baseSender;
        mNick = nick;

        mConnectionList = new HashMap<>();
    }

    public DCCFileConnection getFileConnection(final String fileName) {
        return mConnectionList.get(fileName);
    }

    public void getFile(final DCCPendingSendConnection connection, final File file) {
        final DCCGetConnection getConnection = new DCCGetConnection(connection, mBaseSender, this,
                file);
        mConnectionList.put(connection.getArgument(), getConnection);
        getConnection.startConnection();

        getBus().post(new DCCFileGetStartedEvent(this, getConnection));
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
        return mServerConfiguration.getTitle().equals(that.mServerConfiguration.getTitle())
                && mNick.equals(that.mNick);
    }

    @Override
    public int hashCode() {
        int result = mServerConfiguration.getTitle().hashCode();
        result = 31 * result + mNick.hashCode();
        return result;
    }
}