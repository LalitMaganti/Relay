package co.fusionx.relay.sender.relay;

import co.fusionx.relay.packet.dcc.DCCResumePacket;

public class RelayDCCSender {

    private final RelayPacketSender mServerLineSender;

    public RelayDCCSender(final RelayPacketSender serverLineSender) {
        mServerLineSender = serverLineSender;
    }

    public void requestResume(final String dccRequestNick, final String fileName, final int port,
            final long position) {
        mServerLineSender.post(new DCCResumePacket(dccRequestNick, fileName, port, position));
    }
}