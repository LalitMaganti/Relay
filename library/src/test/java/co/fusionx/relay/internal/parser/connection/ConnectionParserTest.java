package co.fusionx.relay.internal.parser.connection;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.internal.base.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ConnectionParserTest {

    @Test
    public void testSaslAuthentication() {
        final ConnectionConfiguration.Builder builder = TestUtils.getFreenodeBuilderSasl();
        try {
            final PipedReader readerForTesting = new PipedReader();
            final PipedWriter writerForParser = new PipedWriter(readerForTesting);

            final BufferedReader bufferedReaderForTesting = new BufferedReader(readerForTesting);
            final BufferedWriter bufferedWriterForParser = new BufferedWriter(writerForParser);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}