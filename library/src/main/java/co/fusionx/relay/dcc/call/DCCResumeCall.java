package co.fusionx.relay.dcc.call;

import co.fusionx.relay.call.Call;

public class DCCResumeCall extends Call {

    public final static String DCC_RESUME = "PRIVMSG %1$s :\u0001DCC RESUME %2$s %3$d %4$d\u0001";

    private final String mNick;

    private final String mFileName;

    private final int mPort;

    private final long mPosition;

    public DCCResumeCall(final String nick, final String fileName, final int port,
            final long position) {
        mNick = nick;
        mFileName = fileName;
        mPort = port;
        mPosition = position;
    }

    @Override
    public String getLineToSendServer() {
        return String.format(DCC_RESUME, mNick, mFileName, mPort, mPosition);
    }
}
