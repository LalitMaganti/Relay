package co.fusionx.relay.dcc.pending;

import co.fusionx.relay.internal.dcc.RelayDCCManager;

public class DCCPendingChatConnection extends DCCPendingConnection {

    public DCCPendingChatConnection(final String dccRequestNick, final RelayDCCManager manager,
            final String ip, final int port, final String argument, final long size) {
        super(dccRequestNick, manager, ip, port, argument, size);
    }

    public void acceptConnection() {
        mManager.acceptDCCConnection(this);
    }
}