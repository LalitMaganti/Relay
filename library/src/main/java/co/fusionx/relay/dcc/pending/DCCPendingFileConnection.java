package co.fusionx.relay.dcc.pending;

import java.io.File;

import co.fusionx.relay.dcc.RelayDCCManager;

public class DCCPendingFileConnection extends DCCPendingConnection {

    public DCCPendingFileConnection(final String dccRequestNick, final RelayDCCManager manager,
            final String ip, final int port, final String argument, final long size) {
        super(dccRequestNick, manager, ip, port, argument, size);
    }

    public void acceptConnection(final File file) {
        mManager.acceptDCCConnection(this, file);
    }
}