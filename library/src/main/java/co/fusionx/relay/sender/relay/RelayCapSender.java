package co.fusionx.relay.sender.relay;

import co.fusionx.relay.packet.server.cap.CAPEndPacket;
import co.fusionx.relay.packet.server.cap.CAPPlainSASLAuthPacket;
import co.fusionx.relay.packet.server.cap.CAPRequestPlainSaslAuthPacket;
import co.fusionx.relay.packet.server.cap.CAPRequestSASLPacket;
import co.fusionx.relay.packet.server.cap.CAPSupportedPacket;

public class RelayCapSender {

    private final RelayPacketSender mRelayPacketSender;

    public RelayCapSender(final RelayPacketSender relayPacketSender) {
        mRelayPacketSender = relayPacketSender;
    }

    public void sendSupportedCAP() {
        mRelayPacketSender.post(new CAPSupportedPacket());
    }

    public void sendEndCap() {
        mRelayPacketSender.post(new CAPEndPacket());
    }

    public void requestSasl() {
        mRelayPacketSender.post(new CAPRequestSASLPacket());
    }

    public void sendPlainSaslAuthentication() {
        mRelayPacketSender.post(new CAPRequestPlainSaslAuthPacket());
    }

    public void sendSaslPlainAuthentication(final String saslUsername, final String saslPassword) {
        mRelayPacketSender.post(new CAPPlainSASLAuthPacket(saslUsername, saslPassword));
    }
}