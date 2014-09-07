package co.fusionx.relay.internal.sender;

import co.fusionx.relay.internal.packet.server.ctcp.response.ERRMSGResponsePacket;
import co.fusionx.relay.internal.packet.server.ctcp.response.FingerResponsePacket;
import co.fusionx.relay.internal.packet.server.ctcp.response.PingResponsePacket;
import co.fusionx.relay.internal.packet.server.ctcp.response.TimeResponsePacket;
import co.fusionx.relay.internal.packet.server.ctcp.response.VersionResponsePacket;

public class RelayCtcpResponseSender {

    private final RelayBaseSender mRelayBaseSender;

    public RelayCtcpResponseSender(final RelayBaseSender relayBaseSender) {
        mRelayBaseSender = relayBaseSender;
    }

    public void sendFingerResponse(final String nick, final String realName) {
        mRelayBaseSender.sendPacket(new FingerResponsePacket(nick, realName));
    }

    public void sendVersionResponse(final String nick) {
        mRelayBaseSender.sendPacket(new VersionResponsePacket(nick));
    }

    public void sendErrMsgResponse(final String nick, final String query) {
        mRelayBaseSender.sendPacket(new ERRMSGResponsePacket(nick, query));
    }

    public void sendPingResponse(final String nick, final String timestamp) {
        mRelayBaseSender.sendPacket(new PingResponsePacket(nick, timestamp));
    }

    public void sendTimeResponse(final String nick) {
        mRelayBaseSender.sendPacket(new TimeResponsePacket(nick));
    }
}