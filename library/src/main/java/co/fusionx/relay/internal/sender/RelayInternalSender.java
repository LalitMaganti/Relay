package co.fusionx.relay.internal.sender;

import co.fusionx.relay.internal.packet.server.NickServPasswordPacket;
import co.fusionx.relay.internal.packet.server.QuitPacket;
import co.fusionx.relay.internal.packet.server.UserPacket;
import co.fusionx.relay.internal.packet.server.internal.PongPacket;
import co.fusionx.relay.internal.packet.server.internal.ServerPasswordPacket;

public class RelayInternalSender {

    private final RelayBaseSender mRelayBaseSender;

    public RelayInternalSender(final RelayBaseSender relayBaseSender) {
        mRelayBaseSender = relayBaseSender;
    }

    public void pongServer(final String source) {
        mRelayBaseSender.sendPacket(new PongPacket(source));
    }

    public void sendServerPassword(final String password) {
        mRelayBaseSender.sendPacket(new ServerPasswordPacket(password));
    }

    public void sendNickServPassword(final String password) {
        mRelayBaseSender.sendPacket(new NickServPasswordPacket(password));
    }

    public void sendUser(final String serverUserName, final String realName) {
        mRelayBaseSender.sendPacket(new UserPacket(serverUserName, realName));
    }

    public void quitServer(final String quitReason) {
        mRelayBaseSender.sendPacket(new QuitPacket(quitReason));
    }
}