package co.fusionx.relay.parser;

import java.util.Map;

public interface ParserFactory {

    public Map<Integer, ReplyCodeParser> getReplyCodeParsers();

    public Map<String, CommandParser> getCommandParsers();
}