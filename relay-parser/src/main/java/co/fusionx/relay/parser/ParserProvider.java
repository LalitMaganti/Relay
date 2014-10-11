package co.fusionx.relay.parser;

import java.util.Map;

public interface ParserProvider {

    public Map<Integer, ReplyCodeParser> getReplyCodeParsers();

    public Map<String, CommandParser> getCommandParsers();
}