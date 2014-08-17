package co.fusionx.relay.dcc.file;

import co.fusionx.relay.dcc.DCCConnection;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;

public abstract class DCCFileConnection extends DCCConnection {

    private final DCCFileConversation mFileConversation;

    protected long mProgress;

    public DCCFileConnection(final DCCPendingConnection pendingConnection,
            final DCCFileConversation fileConversation) {
        super(pendingConnection);

        mFileConversation = fileConversation;
    }

    public long getProgress() {
        return mProgress;
    }

    void setProgress(final long progress) {
        mProgress = progress;
    }
}