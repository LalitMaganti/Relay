package co.fusionx.relay.util;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

    public static void closeQuietly(final Closeable fileOutput) {
        if (fileOutput == null) {
            return;
        }
        try {
            fileOutput.close();
        } catch (IOException e) {
            // Ignore
        }
    }
}