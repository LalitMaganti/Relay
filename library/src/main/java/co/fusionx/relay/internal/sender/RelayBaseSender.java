package co.fusionx.relay.internal.sender;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import co.fusionx.relay.internal.packet.Packet;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayBaseSender {

    private final ExecutorService mExecutorService;

    private BufferedWriter mBufferedWriter;

    public RelayBaseSender() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    void sendPacket(final Packet packet) {
        final String line = packet.getLine();
        mExecutorService.submit(() -> sendLine(line));
    }

    private void sendLine(final String line) {
        if (mBufferedWriter == null) {
            getPreferences().logServerLine(line);
            return;
        }

        try {
            mBufferedWriter.write(String.format("%s\r\n", line));
            mBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onOutputStreamCreated(final BufferedWriter writer) {
        mBufferedWriter = writer;
    }

    public void onConnectionTerminated() {
        mBufferedWriter = null;
    }
}