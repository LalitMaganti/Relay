package co.fusionx.relay.sender.relay;

import android.os.Handler;

import java.io.BufferedWriter;
import java.io.IOException;

import co.fusionx.relay.packet.Packet;
import co.fusionx.relay.misc.RelayConfigurationProvider;

public class RelayPacketSender {

    private final Handler mCallHandler;

    private BufferedWriter mBufferedWriter;

    public RelayPacketSender(final Handler callHandler) {
        mCallHandler = callHandler;
    }

    void writeLineToServer(final String line) {
        if (mBufferedWriter == null) {
            RelayConfigurationProvider.getPreferences().logServerLine(line);
            return;
        }

        try {
            mBufferedWriter.write(line + "\r\n");
            mBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void post(final Packet packet) {
        mCallHandler.post(() -> postImmediately(packet));
    }

    void postImmediately(final Packet packet) {
        writeLineToServer(packet.getLineToSendServer());
    }

    public void onOutputStreamCreated(final BufferedWriter writer) {
        mBufferedWriter = writer;
    }

    public void onConnectionTerminated() {
        mBufferedWriter = null;
    }
}