package co.fusionx.relay.packet.dcc;

import co.fusionx.relay.packet.Packet;

public class DCCResumePacket implements Packet {

    public final static String DCC_RESUME = "PRIVMSG %1$s :\u0001DCC RESUME %2$s %3$d %4$d\u0001";

    private final String mNick;

    private final String mFileName;

    private final int mPort;

    private final long mPosition;

    public DCCResumePacket(final String nick, final String fileName, final int port,
            final long position) {
        mNick = nick;
        mFileName = fileName;
        mPort = port;
        mPosition = position;
    }

    @Override
    public String getLine() {
        return String.format(DCC_RESUME, mNick, mFileName, mPort, mPosition);
    }
}
