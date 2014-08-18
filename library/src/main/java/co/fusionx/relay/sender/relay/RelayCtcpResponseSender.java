package co.fusionx.relay.sender.relay;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.call.server.ctcp.response.ERRMSGResponseCall;
import co.fusionx.relay.call.server.ctcp.response.FingerResponseCall;
import co.fusionx.relay.call.server.ctcp.response.PingResponseCall;
import co.fusionx.relay.call.server.ctcp.response.TimeResponseCall;
import co.fusionx.relay.call.server.ctcp.response.VersionResponseCall;

public class RelayCtcpResponseSender {

    private final RelayServerLineSender mRelayServerLineSender;

    public RelayCtcpResponseSender(final RelayServerLineSender relayServerLineSender) {
        mRelayServerLineSender = relayServerLineSender;
    }

    public void sendFingerResponse(final String nick, final RelayServer server) {
        mRelayServerLineSender.post(new FingerResponseCall(nick, server));
    }

    public void sendVersionResponse(final String nick) {
        mRelayServerLineSender.post(new VersionResponseCall(nick));
    }

    public void sendErrMsgResponse(final String nick, final String query) {
        mRelayServerLineSender.post(new ERRMSGResponseCall(nick, query));
    }

    public void sendPingResponse(final String nick, final String timestamp) {
        mRelayServerLineSender.post(new PingResponseCall(nick, timestamp));
    }

    public void sendTimeResponse(final String nick) {
        mRelayServerLineSender.post(new TimeResponseCall(nick));
    }
}