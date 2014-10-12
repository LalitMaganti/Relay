package co.fusionx.relay.internal.parser;

import java.io.BufferedReader;
import java.io.IOException;

import javax.inject.Inject;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.parser.InputParser;
import co.fusionx.relay.provider.NickProvider;

public class BufferedInputParser {

    private final InputParser mInputParser;

    @Inject
    public BufferedInputParser(final InputParser inputParser) {
        mInputParser = inputParser;
    }

    /**
     * A loop which reads each line from the server as it is received and passes it on to parse
     *
     * @param reader the reader associated with the server stream
     */
    public void parseMain(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            mInputParser.parseLine(line);
        }
    }
}