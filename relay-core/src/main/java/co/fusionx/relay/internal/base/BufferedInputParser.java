package co.fusionx.relay.internal.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import co.fusionx.relay.parser.InputParser;
import co.fusionx.relay.provider.DebuggingProvider;

public class BufferedInputParser {

    private final DebuggingProvider mDebuggingProvider;

    private final InputParser mInputParser;

    private final AtomicBoolean mContinueParsing = new AtomicBoolean(true);

    @Inject
    public BufferedInputParser(final DebuggingProvider debuggingProvider,
            final InputParser inputParser) {
        mDebuggingProvider = debuggingProvider;
        mInputParser = inputParser;
    }

    /**
     * A loop which reads each line from the server as it is received and passes it on to parse
     *
     * @param reader the reader associated with the server stream
     */
    public void parseMain(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && mContinueParsing.get()) {
            mDebuggingProvider.logLineFromServer(line);
            mInputParser.parseLine(line);
        }
    }

    public void stopParsing() {
        mContinueParsing.set(false);
    }
}