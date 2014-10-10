package co.fusionx.relay.base;

import co.fusionx.relay.core.DCCManager;
import co.fusionx.relay.core.InternalDCCManager;

public class RelayDCCPendingConnection {

    protected final InternalDCCManager mManager;

    protected final String mIP;

    protected final int mPort;

    protected final String mArgument;

    protected final long mSize;

    private final String mDccRequestNick;

    public RelayDCCPendingConnection(final String dccRequestNick, final InternalDCCManager manager,
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
        } else if (!(o instanceof RelayDCCPendingConnection)) {
            return false;
        }

        final RelayDCCPendingConnection that = (RelayDCCPendingConnection) o;
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