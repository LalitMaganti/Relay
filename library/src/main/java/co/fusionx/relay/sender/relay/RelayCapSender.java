package co.fusionx.relay.sender.relay;

import co.fusionx.relay.packet.server.cap.CAPEndPacket;
import co.fusionx.relay.packet.server.cap.CAPLSPacket;
import co.fusionx.relay.packet.server.cap.CAPPlainSASLAuthPacket;
import co.fusionx.relay.packet.server.cap.CAPRequestPlainSaslAuthPacket;
import co.fusionx.relay.packet.server.cap.CAPRequestSASLPacket;

public class RelayCapSender {

    private final RelayPacketSender mRelayPacketSender;

    public RelayCapSender(final RelayPacketSender relayPacketSender) {
        mRelayPacketSender = relayPacketSender;
    }

    public void sendLs() {
        mRelayPacketSender.sendPacket(new CAPLSPacket());
    }

    public void sendEnd() {
        mRelayPacketSender.sendPacket(new CAPEndPacket());
    }

    public void sendSaslRequest() {
        mRelayPacketSender.sendPacket(new CAPRequestSASLPacket());
    }

    public void sendPlainAuthenticationRequest() {
        mRelayPacketSender.sendPacket(new CAPRequestPlainSaslAuthPacket());
    }

    public void sendSaslPlainAuthentication(final String saslUsername, final String saslPassword) {
        mRelayPacketSender.sendPacket(new CAPPlainSASLAuthPacket(saslUsername, saslPassword));
    }
}