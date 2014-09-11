package co.fusionx.relay.internal.sender;

import com.google.common.base.Optional;

import javax.inject.Inject;

import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.packet.server.JoinPacket;
import co.fusionx.relay.internal.packet.server.NickChangePacket;
import co.fusionx.relay.internal.packet.server.RawPacket;
import co.fusionx.relay.internal.packet.server.WhoisPacket;
import co.fusionx.relay.sender.ServerSender;
import dagger.Lazy;

public class RelayServerSender implements ServerSender {

    private final PacketSender mPacketSender;

    private final InternalQueryUserGroup mQueryManager;

    private final Lazy<InternalServer> mInternalServer;

    @Inject
    public RelayServerSender(final PacketSender packetSender,
            final InternalQueryUserGroup queryManager, final Lazy<InternalServer> internalServer) {
        mPacketSender = packetSender;
        mQueryManager = queryManager;
        mInternalServer = internalServer;
    }

    @Override
    public void sendQuery(final String nick, final String message) {
        final Optional<InternalQueryUser> optional = mQueryManager.getQueryUser(nick);
        final InternalQueryUser user = optional.or(mQueryManager.addQueryUser(nick));
        if (!optional.isPresent()) {
            final InternalServer server = mInternalServer.get();
            server.postEvent(new NewPrivateMessageEvent(server, user));
        }
        user.sendMessage(message);
    }

    @Override
    public void sendJoin(final String channelName) {
        mPacketSender.sendPacket(new JoinPacket(channelName));
    }

    @Override
    public void sendNick(final String newNick) {
        mPacketSender.sendPacket(new NickChangePacket(newNick));
    }

    @Override
    public void sendWhois(final String nick) {
        mPacketSender.sendPacket(new WhoisPacket(nick));
    }

    @Override
    public void sendRawLine(final String rawLine) {
        mPacketSender.sendPacket(new RawPacket(rawLine));
    }
}