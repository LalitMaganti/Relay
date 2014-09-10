package co.fusionx.relay.internal.sender;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import co.fusionx.relay.core.SettingsProvider;
import co.fusionx.relay.internal.packet.Packet;

public class PacketSender {

    private final Object mLock = new Object();

    private final SettingsProvider mSettingsProvider;

    private final ExecutorService mExecutorService;

    private BufferedWriter mBufferedWriter;

    public PacketSender(final SettingsProvider settingsProvider,
            final ExecutorService executorService) {
        mSettingsProvider = settingsProvider;
        mExecutorService = executorService;
    }

    public void sendPacket(final Packet packet) {
        final String line = packet.getLine();
        mExecutorService.submit(() -> sendLine(line));
    }

    public void onOutputStreamCreated(final BufferedWriter writer) {
        synchronized (mLock) {
            mBufferedWriter = writer;
        }
    }

    public void onConnectionTerminated() {
        synchronized (mLock) {
            mBufferedWriter = null;
        }
    }

    private void sendLine(final String line) {
        synchronized (mLock) {
            if (mBufferedWriter == null) {
                mSettingsProvider.logNonFatalError(line);
                return;
            }

            try {
                mBufferedWriter.write(String.format("%s\r\n", line));
                mBufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}