package co.fusionx.relay.internal.parser.connection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.internal.base.TestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class ConnectionParserTest {

    @Test
    public void testSaslAuthentication() {
        final ServerConfiguration.Builder builder = TestUtils.getFreenodeBuilderSasl();
        final RelayServer server = TestUtils.getServerFromConfiguration(builder.build());
        try {
            final PipedReader readerForTesting = new PipedReader();
            final PipedWriter writerForParser = new PipedWriter(readerForTesting);

            final BufferedReader bufferedReaderForTesting = new BufferedReader(readerForTesting);
            final BufferedWriter bufferedWriterForParser = new BufferedWriter(writerForParser);
            server.onOutputStreamCreated(bufferedWriterForParser);

            final ConnectionParser connectionParser = new ConnectionParser(server, null);
            connectionParser.parseLine(":test.server CAP * LS :sasl");

            if (!bufferedReaderForTesting.ready()) {
                fail("SASL not requested");
            }
            // Check that we request the correct line
            assertThat(bufferedReaderForTesting.readLine())
                    .isEqualTo("CAP REQ :sasl");

            connectionParser.parseLine(":test.server CAP * ACK :sasl");

            if (!bufferedReaderForTesting.ready()) {
                fail("SASL not working");
            }
            // Check that we request the correct line
            assertThat(bufferedReaderForTesting.readLine())
                    .isEqualTo("AUTHENTICATE PLAIN");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}