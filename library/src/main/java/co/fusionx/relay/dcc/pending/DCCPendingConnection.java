package co.fusionx.relay.dcc.pending;

import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.dcc.RelayDCCManager;

public class DCCPendingConnection {

    protected final RelayDCCManager mManager;

    protected final String mIP;

    protected final int mPort;

    protected final String mArgument;

    protected final long mSize;

    private final String mDccRequestNick;

    public DCCPendingConnection(final String dccRequestNick, final RelayDCCManager manager,
            final String ip, final int port, final String argument, final long size) {
        mDccRequestNick = dccRequestNick;
        mManager = manager;
        mIP = ip;
        mPort = port;
        mArgument = argument;
        mSize = size;

        mManager.addPendingConnection(this);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof DCCPendingConnection)) {
            return false;
        }

        final DCCPendingConnection that = (DCCPendingConnection) o;
        return mPort == that.mPort && mDccRequestNick.equals(that.mDccRequestNick)
                && mIP.equals(that.mIP) && mManager.equals(that.mManager);
    }

    @Override
    public int hashCode() {
        int result = mDccRequestNick.hashCode();
        result = 31 * result + mManager.hashCode();
        result = 31 * result + mIP.hashCode();
        result = 31 * result + mPort;
        return result;
    }
}