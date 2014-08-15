package co.fusionx.relay.dcc.connection;

import java.io.File;
import java.io.IOException;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.dcc.pending.DCCPendingFileConnection;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class DCCFileConnection extends DCCConnection {

    private final File mFile;

    public DCCFileConnection(final RelayServer relayServer,
            final DCCPendingFileConnection pendingConnection, final File file) {
        super(relayServer, pendingConnection);

        mFile = file;
    }

    @Override
    protected void connect() {
        try {
            final BufferedSource source = Okio.buffer(Okio.source(mSocket));
            final BufferedSink sink = Okio.buffer(Okio.sink(mFile));

            final int bufferSize = 2048;
            long totalBytesWritten = 0;
            while (source.read(source.buffer(), bufferSize) != -1) {
                long emitByteCount = source.buffer().completeSegmentByteCount();
                if (emitByteCount > 0) {
                    totalBytesWritten += emitByteCount;
                    sink.write(source.buffer(), emitByteCount);
                }
            }
            if (source.buffer().size() > 0) {
                totalBytesWritten += source.buffer().size();
                sink.write(source.buffer(), source.buffer().size());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}