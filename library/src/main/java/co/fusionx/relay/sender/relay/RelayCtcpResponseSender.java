package co.fusionx.relay.sender.relay;

import co.fusionx.relay.packet.server.ctcp.response.ERRMSGResponsePacket;
import co.fusionx.relay.packet.server.ctcp.response.FingerResponsePacket;
import co.fusionx.relay.packet.server.ctcp.response.PingResponsePacket;
import co.fusionx.relay.packet.server.ctcp.response.TimeResponsePacket;
import co.fusionx.relay.packet.server.ctcp.response.VersionResponsePacket;

public class RelayCtcpResponseSender {

    private final RelayPacketSender mRelayPacketSender;

    public RelayCtcpResponseSender(final RelayPacketSender relayPacketSender) {
        mRelayPacketSender = relayPacketSender;
    }

    public void sendFingerResponse(final String nick, final String realName) {
        mRelayPacketSender.post(new FingerResponsePacket(nick, realName));
    }

    public void sendVersionResponse(final String nick) {
        mRelayPacketSender.post(new VersionResponsePacket(nick));
    }

    public void sendErrMsgResponse(final String nick, final String query) {
        mRelayPacketSender.post(new ERRMSGResponsePacket(nick, query));
    }

    public void sendPingResponse(final String nick, final String timestamp) {
        mRelayPacketSender.post(new PingResponsePacket(nick, timestamp));
    }

    public void sendTimeResponse(final String nick) {
        mRelayPacketSender.post(new TimeResponsePacket(nick));
    }
}