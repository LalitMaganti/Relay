package co.fusionx.relay.parser;

import java.util.List;

/**
 * CodeParser represents a parser which can parse a reply code as specified by RFC2812 and co.
 */
public interface ReplyCodeParser {

    /**
     * Parses a line containing a reply from the server
     *
     * @param parsedArray the data in the command (this excludes any possible prefix and the
     *                    keyword of the command itself)
     * @param code        contains the code this parser was matched with
     */
    public void parseReplyCode(final List<String> parsedArray, final int code);

    /**
     * Returns the list of codes this parser can parse
     *
     * @return the list of codes it can parse
     */
    public List<Integer> parsableCodes();
}