package co.fusionx.relay.dcc.pending;

import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.dcc.RelayDCCManager;

public class DCCPendingConnection {

    private final String mDccRequestNick;

    protected final RelayDCCManager mManager;

    protected final String mIP;

    protected final int mPort;

    protected final String mArgument;

    protected final long mSize;

    public DCCPendingConnection(final String dccRequestNick, final RelayDCCManager manager,
            final String ip, final int port, final String argument, final long size) {
        mDccRequestNick = dccRequestNick;

        mManager = manager;
        mManager.addPendingConnection(this);

        mIP = ip;
        mPort = port;
        mArgument = argument;
        mSize = size;
    }

    public void declineConnection() {
        mManager.declineDCCConnection(this);
    }

    public String getDccRequestNick() {
        return mDccRequestNick;
    }

    public String getIP() {
        return mIP;
    }

    public int getPort() {
        return mPort;
    }

    public String getArgument() {
        return mArgument;
    }

    public long getSize() {
        return mSize;
    }

    public DCCManager getManager() {
        return mManager;
    }
}