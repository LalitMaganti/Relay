package co.fusionx.relay.internal.sender;

import co.fusionx.relay.internal.packet.dcc.DCCResumePacket;

public class DCCPacketSender {

    private final PacketSender mServerLineSender;

    public DCCPacketSender(final PacketSender serverLineSender) {
        mServerLineSender = serverLineSender;
    }

    public void requestResume(final String dccRequestNick, final String fileName, final int port,
            final long position) {
        mServerLineSender.sendPacket(new DCCResumePacket(dccRequestNick, fileName, port, position));
    }
}