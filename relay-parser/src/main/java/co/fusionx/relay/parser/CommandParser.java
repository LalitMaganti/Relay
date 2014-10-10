package co.fusionx.relay.parser;

import java.util.List;

/**
 * CommandParser represents a parser which can parse a command as specified by RFC2812 and co.
 */
public interface CommandParser {

    /**
     * Parses a line containing a command from the server
     *
     * @param parsedArray the data in the command (this excludes any possible prefix and the
     *                    keyword of the command itself)
     * @param prefix      contains the prefix if sent by the server - this can be empty or null
     *                    and implementations SHOULD NOT assume that the prefix is non-null
     */
    public void parseCommand(final List<String> parsedArray, final String prefix);
}