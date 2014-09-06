package co.fusionx.relay.sender.relay;

import co.fusionx.relay.packet.server.cap.CAPEndPacket;
import co.fusionx.relay.packet.server.cap.CAPLSPacket;
import co.fusionx.relay.packet.server.cap.CAPPlainSASLAuthPacket;
import co.fusionx.relay.packet.server.cap.CAPRequestSASLPacket;
import co.fusionx.relay.packet.server.cap.CapRequestCapabilitiesPacket;
import co.fusionx.relay.packet.server.cap.SaslRequestPlainAuthPacket;

public class RelayCapSender {

    private final RelayPacketSender mSender;

    public RelayCapSender(final RelayPacketSender sender) {
        mSender = sender;
    }

    public void sendLs() {
        mSender.sendPacket(new CAPLSPacket());
    }

    public void sendEnd() {
        mSender.sendPacket(new CAPEndPacket());
    }

    public void sendRequestSasl() {
        mSender.sendPacket(new CAPRequestSASLPacket());
    }

    public void sendPlainAuthenticationRequest() {
        mSender.sendPacket(new SaslRequestPlainAuthPacket());
    }

    public void sendSaslPlainAuthentication(final String saslUsername, final String saslPassword) {
        mSender.sendPacket(new CAPPlainSASLAuthPacket(saslUsername, saslPassword));
    }

    public void sendRequestCapabilities(final String capabilities) {
        mSender.sendPacket(new CapRequestCapabilitiesPacket(capabilities));
    }
}