package co.fusionx.relay.internal.sender;

import co.fusionx.relay.internal.packet.server.ctcp.response.ERRMSGResponsePacket;
import co.fusionx.relay.internal.packet.server.ctcp.response.FingerResponsePacket;
import co.fusionx.relay.internal.packet.server.ctcp.response.PingResponsePacket;
import co.fusionx.relay.internal.packet.server.ctcp.response.TimeResponsePacket;
import co.fusionx.relay.internal.packet.server.ctcp.response.VersionResponsePacket;

public class CtcpResponsePacketSender {

    private final PacketSender mRelayPacketSender;

    public CtcpResponsePacketSender(final PacketSender relayPacketSender) {
        mRelayPacketSender = relayPacketSender;
    }

    public void sendFingerResponse(final String nick, final String realName) {
        mRelayPacketSender.sendPacket(new FingerResponsePacket(nick, realName));
    }

    public void sendVersionResponse(final String nick) {
        mRelayPacketSender.sendPacket(new VersionResponsePacket(nick));
    }

    public void sendErrMsgResponse(final String nick, final String query) {
        mRelayPacketSender.sendPacket(new ERRMSGResponsePacket(nick, query));
    }

    public void sendPingResponse(final String nick, final String timestamp) {
        mRelayPacketSender.sendPacket(new PingResponsePacket(nick, timestamp));
    }

    public void sendTimeResponse(final String nick) {
        mRelayPacketSender.sendPacket(new TimeResponsePacket(nick));
    }
}