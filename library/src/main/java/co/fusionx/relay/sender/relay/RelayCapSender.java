package co.fusionx.relay.sender.relay;

import co.fusionx.relay.call.server.cap.CAPEndCall;
import co.fusionx.relay.call.server.cap.CAPPlainSASLAuthCall;
import co.fusionx.relay.call.server.cap.CAPRequestPlainSaslAuthCall;
import co.fusionx.relay.call.server.cap.CAPRequestSASLCall;
import co.fusionx.relay.call.server.cap.CAPSupportedCall;

public class RelayCapSender {

    private final RelayServerLineSender mRelayServerLineSender;

    public RelayCapSender(final RelayServerLineSender relayServerLineSender) {
        mRelayServerLineSender = relayServerLineSender;
    }

    public void sendSupportedCAP() {
        mRelayServerLineSender.post(new CAPSupportedCall());
    }

    public void sendEndCap() {
        mRelayServerLineSender.post(new CAPEndCall());
    }

    public void requestSasl() {
        mRelayServerLineSender.post(new CAPRequestSASLCall());
    }

    public void sendPlainSaslAuthentication() {
        mRelayServerLineSender.post(new CAPRequestPlainSaslAuthCall());
    }

    public void sendSaslPlainAuthentication(final String saslUsername, final String saslPassword) {
        mRelayServerLineSender.post(new CAPPlainSASLAuthCall(saslUsername, saslPassword));
    }
}