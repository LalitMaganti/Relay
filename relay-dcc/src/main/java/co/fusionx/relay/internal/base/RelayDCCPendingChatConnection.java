package co.fusionx.relay.internal.base;

import co.fusionx.relay.internal.core.InternalDCCManager;

public class RelayDCCPendingChatConnection extends RelayDCCPendingConnection {

    public RelayDCCPendingChatConnection(final String dccRequestNick,
            final InternalDCCManager manager,
            final String ip, final int port, final String argument, final long size) {
        super(dccRequestNick, manager, ip, port, argument, size);
    }

    public void acceptConnection() {
        mManager.acceptDCCConnection(this);
    }
}