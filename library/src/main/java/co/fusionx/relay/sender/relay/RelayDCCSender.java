package co.fusionx.relay.sender.relay;

import co.fusionx.relay.call.dcc.DCCResumeCall;

public class RelayDCCSender {

    private final RelayServerLineSender mServerLineSender;

    public RelayDCCSender(final RelayServerLineSender serverLineSender) {
        mServerLineSender = serverLineSender;
    }

    public void requestResume(final String dccRequestNick, final String fileName, final int port,
            final long position) {
        mServerLineSender.post(new DCCResumeCall(dccRequestNick, fileName, port, position));
    }
}