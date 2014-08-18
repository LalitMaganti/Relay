package co.fusionx.relay.sender.relay;

import android.os.Handler;

import java.io.BufferedWriter;
import java.io.IOException;

import co.fusionx.relay.call.Call;
import co.fusionx.relay.misc.RelayConfigurationProvider;

public class RelayServerLineSender {

    private final Handler mCallHandler;

    private BufferedWriter mBufferedWriter;

    public RelayServerLineSender(final Handler callHandler) {
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

    void post(final Call call) {
        mCallHandler.post(() -> postImmediately(call));
    }

    void postImmediately(final Call call) {
        writeLineToServer(call.getLineToSendServer());
    }

    public void onOutputStreamCreated(final BufferedWriter writer) {
        mBufferedWriter = writer;
    }

    public void onConnectionTerminated() {
        mBufferedWriter = null;
    }
}