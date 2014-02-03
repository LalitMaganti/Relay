package com.fusionx.relay.writers;

import java.io.IOException;
import java.io.Writer;

public abstract class RawWriter {

    private final Writer streamWriter;

    RawWriter(final Writer writer) {
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