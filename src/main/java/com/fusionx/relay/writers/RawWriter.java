package com.fusionx.relay.writers;

import java.io.IOException;
import java.io.OutputStreamWriter;

abstract class RawWriter {

    private final OutputStreamWriter streamWriter;

    RawWriter(final OutputStreamWriter writer) {
        streamWriter = writer;
    }

    void writeLineToServer(final String line) {
        try {
            streamWriter.write(line + "\r\n");
            streamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}