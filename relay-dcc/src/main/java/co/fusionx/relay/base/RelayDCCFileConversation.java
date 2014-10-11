package co.fusionx.relay.base;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.event.file.DCCFileEvent;
import co.fusionx.relay.event.file.DCCFileGetStartedEvent;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.base.AbstractConversation;
import co.fusionx.relay.internal.core.Postable;
import co.fusionx.relay.internal.sender.PacketSender;

public class RelayDCCFileConversation extends AbstractConversation<DCCFileEvent> {

    private final ConnectionConfiguration mConnectionConfiguration;

    private final PacketSender mPacketSender;

    private final String mNick;

    private final Map<String, RelayDCCFileConnection> mConnectionList;

    public RelayDCCFileConversation(final Postable<Event> bus,
            final ConnectionConfiguration connectionConfiguration,
            final PacketSender packetSender, final String nick) {
        super(bus);

        mConnectionConfiguration = connectionConfiguration;
        mPacketSender = packetSender;
        mNick = nick;

        mConnectionList = new HashMap<>();
    }

    public RelayDCCFileConnection getFileConnection(final String fileName) {
        return mConnectionList.get(fileName);
    }

    public void getFile(final RelayDCCPendingSendConnection connection, final File file) {
        final RelayDCCGetConnection getConnection = new RelayDCCGetConnection(connection, mPacketSender, this,
                file);
        mConnectionList.put(connection.getArgument(), getConnection);
        getConnection.startConnection();

        postEvent(new DCCFileGetStartedEvent(this, getConnection));
    }

    public Collection<RelayDCCFileConnection> getFileConnections() {
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
        } else if (!(o instanceof RelayDCCFileConversation)) {
            return false;
        }

        final RelayDCCFileConversation that = (RelayDCCFileConversation) o;
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