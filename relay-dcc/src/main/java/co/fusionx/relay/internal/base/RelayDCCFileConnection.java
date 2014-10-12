package co.fusionx.relay.internal.base;

import co.fusionx.relay.core.DCCConnection;
import co.fusionx.relay.event.file.DCCFileProgressEvent;

public abstract class RelayDCCFileConnection extends DCCConnection {

    protected final RelayDCCFileConversation mFileConversation;

    protected long mBytesTransferred;

    public RelayDCCFileConnection(final RelayDCCPendingConnection pendingConnection,
            final RelayDCCFileConversation fileConversation) {
        super(pendingConnection);

        mFileConversation = fileConversation;
    }

    public String getFileName() {
        return mPendingConnection.getArgument();
    }

    public long getProgress() {
        return (mBytesTransferred * 100) / mPendingConnection.getSize();
    }

    public long getBytesTransferred() {
        return mBytesTransferred;
    }

    protected void setBytesTransferred(final long bytesTransferred) {
        final long progress = getProgress();
        mBytesTransferred = bytesTransferred;

        final long newProgress = (mBytesTransferred * 100) / mPendingConnection.getSize();
        if (progress != newProgress) {
            mFileConversation.postEvent(new DCCFileProgressEvent(mFileConversation, this,
                    getProgress()));
        }
    }
}