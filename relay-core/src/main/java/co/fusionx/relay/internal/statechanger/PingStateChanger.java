package co.fusionx.relay.internal.statechanger;

import co.fusionx.relay.internal.sender.InternalSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayInternalSender;
import co.fusionx.relay.parser.rfc.PingParser;

public class PingStateChanger implements PingParser.PingObserver {

    private final InternalSender mInternalSender;

    public PingStateChanger(final PacketSender packetSender) {
        mInternalSender = new RelayInternalSender(packetSender);
    }

    @Override
    public void onPing(final String serverHostname) {
        mInternalSender.pongServer(serverHostname);
    }
}