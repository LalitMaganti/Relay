package co.fusionx.relay.dcc.file;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.base.ConnectionConfiguration;
import co.fusionx.relay.dcc.event.file.DCCFileEvent;
import co.fusionx.relay.dcc.event.file.DCCFileGetStartedEvent;
import co.fusionx.relay.dcc.pending.DCCPendingSendConnection;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.base.RelayAbstractConversation;
import co.fusionx.relay.internal.sender.packet.PacketSender;
import co.fusionx.relay.bus.GenericBus;

public class DCCFileConversation extends RelayAbstractConversation<DCCFileEvent> {

    private final ConnectionConfiguration mConnectionConfiguration;

    private final PacketSender mPacketSender;

    private final String mNick;

    private final Map<String, DCCFileConnection> mConnectionList;

    public DCCFileConversation(final GenericBus<Event> bus,
            final ConnectionConfiguration connectionConfiguration,
            final PacketSender packetSender, final String nick) {
        super(bus);

        mConnectionConfiguration = connectionConfiguration;
        mPacketSender = packetSender;
        mNick = nick;

        mConnectionList = new HashMap<>();
    }

    public DCCFileConnection getFileConnection(final String fileName) {
        return mConnectionList.get(fileName);
    }

    public void getFile(final DCCPendingSendConnection connection, final File file) {
        final DCCGetConnection getConnection = new DCCGetConnection(connection, mPacketSender, this,
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
        return mConnectionConfiguration.getTitle().equals(that.mConnectionConfiguration.getTitle())
                && mNick.equals(that.mNick);
    }

    @Override
    public int hashCode() {
        int result = mConnectionConfiguration.getTitle().hashCode();
        result = 31 * result + mNick.hashCode();
        return result;
    }
}