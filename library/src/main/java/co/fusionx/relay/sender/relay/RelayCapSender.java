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
        mRelayPacketSender.sendPacket(new CAPSupportedPacket());
    }

    public void sendEndCap() {
        mRelayPacketSender.sendPacket(new CAPEndPacket());
    }

    public void requestSasl() {
        mRelayPacketSender.sendPacket(new CAPRequestSASLPacket());
    }

    public void sendPlainSaslAuthentication() {
        mRelayPacketSender.sendPacket(new CAPRequestPlainSaslAuthPacket());
    }

    public void sendSaslPlainAuthentication(final String saslUsername, final String saslPassword) {
        mRelayPacketSender.sendPacket(new CAPPlainSASLAuthPacket(saslUsername, saslPassword));
    }
}