package co.fusionx.relay.internal.sender;

import co.fusionx.relay.internal.packet.server.cap.CAPEndPacket;
import co.fusionx.relay.internal.packet.server.cap.CAPLSPacket;
import co.fusionx.relay.internal.packet.server.cap.CAPPlainSASLAuthPacket;
import co.fusionx.relay.internal.packet.server.cap.CAPRequestSASLPacket;
import co.fusionx.relay.internal.packet.server.cap.CapRequestCapabilitiesPacket;
import co.fusionx.relay.internal.packet.server.cap.SaslRequestPlainAuthPacket;

public class RelayCapSender {

    private final RelayBaseSender mSender;

    public RelayCapSender(final RelayBaseSender sender) {
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