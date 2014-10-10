package co.fusionx.relay.internal.sender;

import javax.inject.Inject;

import co.fusionx.relay.internal.packet.server.NickServPasswordPacket;
import co.fusionx.relay.internal.packet.server.QuitPacket;
import co.fusionx.relay.internal.packet.server.UserPacket;
import co.fusionx.relay.internal.packet.server.internal.PongPacket;
import co.fusionx.relay.internal.packet.server.internal.ServerPasswordPacket;

public class RelayInternalSender implements InternalSender {

    private final PacketSender mPacketSender;

    @Inject
    public RelayInternalSender(final PacketSender packetSender) {
        mPacketSender = packetSender;
    }

    @Override
    public void pongServer(final String source) {
        mPacketSender.sendPacket(new PongPacket(source));
    }

    @Override
    public void sendServerPassword(final String password) {
        mPacketSender.sendPacket(new ServerPasswordPacket(password));
    }

    @Override
    public void sendNickServPassword(final String password) {
        mPacketSender.sendPacket(new NickServPasswordPacket(password));
    }

    @Override
    public void sendUser(final String serverUserName, final String realName) {
        mPacketSender.sendPacket(new UserPacket(serverUserName, realName));
    }

    @Override
    public void quitServer(final String quitReason) {
        mPacketSender.sendPacket(new QuitPacket(quitReason));
    }
}