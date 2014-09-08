package co.fusionx.relay.dcc.file;

import co.fusionx.relay.dcc.DCCConnection;
import co.fusionx.relay.dcc.event.file.DCCFileProgressEvent;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public abstract class DCCFileConnection extends DCCConnection {

    protected final DCCFileConversation mFileConversation;

    protected long mBytesTransferred;

    public DCCFileConnection(final DCCPendingConnection pendingConnection,
            final DCCFileConversation fileConversation) {
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
            mFileConversation.getBus().post(new DCCFileProgressEvent(mFileConversation, this,
                        getProgress()));
        }
    }
}