package co.fusionx.relay.internal.sender;

import java.io.BufferedWriter;
import java.io.IOException;

import co.fusionx.relay.internal.packet.Packet;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayBaseSender {

    private BufferedWriter mBufferedWriter;

    void sendPacket(final Packet packet) {
        final String line = packet.getLine();
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