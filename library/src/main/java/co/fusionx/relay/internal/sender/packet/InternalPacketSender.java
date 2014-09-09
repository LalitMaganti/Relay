package co.fusionx.relay.internal.sender.packet;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.internal.packet.server.NickServPasswordPacket;
import co.fusionx.relay.internal.packet.server.QuitPacket;
import co.fusionx.relay.internal.packet.server.UserPacket;
import co.fusionx.relay.internal.packet.server.internal.PongPacket;
import co.fusionx.relay.internal.packet.server.internal.ServerPasswordPacket;

public class InternalPacketSender {

    private final PacketSender mPacketSender;

    @Inject
    public InternalPacketSender(final PacketSender packetSender) {
        mPacketSender = packetSender;
    }

    public void pongServer(final String source) {
        mPacketSender.sendPacket(new PongPacket(source));
    }

    public void sendServerPassword(final String password) {
        mPacketSender.sendPacket(new ServerPasswordPacket(password));
    }

    public void sendNickServPassword(final String password) {
        mPacketSender.sendPacket(new NickServPasswordPacket(password));
    }

    public void sendUser(final String serverUserName, final String realName) {
        mPacketSender.sendPacket(new UserPacket(serverUserName, realName));
    }

    public void quitServer(final String quitReason) {
        mPacketSender.sendPacket(new QuitPacket(quitReason));
    }
}