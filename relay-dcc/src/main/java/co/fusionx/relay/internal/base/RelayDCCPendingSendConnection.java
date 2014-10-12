package co.fusionx.relay.internal.base;

import java.io.File;

import co.fusionx.relay.internal.core.InternalDCCManager;

public class RelayDCCPendingSendConnection extends RelayDCCPendingConnection {

    public RelayDCCPendingSendConnection(final String dccRequestNick,
            final InternalDCCManager manager, final String ip, final int port,
            final String argument, final long size) {
        super(dccRequestNick, manager, ip, port, argument, size);
    }

    public void acceptConnection(final File file) {
        mManager.acceptDCCConnection(this, file);
    }
}