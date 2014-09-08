package co.fusionx.relay.internal.sender.base;

import com.google.common.base.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.base.Server;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.internal.base.RelayQueryUser;
import co.fusionx.relay.internal.base.RelayQueryUserGroup;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.packet.server.JoinPacket;
import co.fusionx.relay.internal.packet.server.NickChangePacket;
import co.fusionx.relay.internal.packet.server.RawPacket;
import co.fusionx.relay.internal.packet.server.WhoisPacket;
import co.fusionx.relay.internal.sender.packet.PacketSender;
import co.fusionx.relay.sender.ServerSender;

@Singleton
public class RelayServerSender implements ServerSender {

    private final PacketSender mPacketSender;

    private final Server mServer;

    private final RelayQueryUserGroup mQueryManager;

    @Inject
    public RelayServerSender(final PacketSender packetSender, final RelayServer server,
            final RelayQueryUserGroup queryManager) {
        mPacketSender = packetSender;
        mServer = server;
        mQueryManager = queryManager;
    }

    @Override
    public void sendQuery(final String nick, final String message) {
        final Optional<RelayQueryUser> optional = mQueryManager.getQueryUser(nick);
        final RelayQueryUser user = optional.or(mQueryManager.addQueryUser(nick));
        if (!optional.isPresent()) {
            mServer.getBus().post(new NewPrivateMessageEvent(mServer, user));
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