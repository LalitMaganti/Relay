package co.fusionx.relay.internal.provider;

import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.internal.constants.Commands;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ParserFactory;
import co.fusionx.relay.parser.ReplyCodeParser;
import co.fusionx.relay.parser.ircv3.NickPrefixNameParser;
import co.fusionx.relay.parser.rfc.InviteParser;
import co.fusionx.relay.parser.rfc.JoinParser;
import co.fusionx.relay.parser.rfc.NickParser;
import co.fusionx.relay.parser.rfc.PartParser;
import co.fusionx.relay.parser.rfc.PingParser;
import co.fusionx.relay.parser.rfc.QuitParser;
import co.fusionx.relay.parser.rfc.TopicParser;

public class CoreParserFactory implements ParserFactory {

    private final ParserObserverProvider mParserObserverProvider;

    public CoreParserFactory(final ParserObserverProvider parserObserverProvider) {
        mParserObserverProvider = parserObserverProvider;
    }

    @Override
    public Map<String, CommandParser> getCommandParsers() {
        final HashMap<String, CommandParser> map = new HashMap<>();

        map.put(Commands.TOPIC, new InviteParser(mParserObserverProvider.getInviteObserver()));
        map.put(Commands.JOIN, new JoinParser(mParserObserverProvider.getJoinObserver()));
        map.put(Commands.NICK, new NickParser(mParserObserverProvider.getNickProvider()));
        map.put(Commands.PART, new PartParser(mParserObserverProvider.getPartObserver()));
        map.put(Commands.PING, new PingParser(mParserObserverProvider.getPingObserver()));
        map.put(Commands.TOPIC, new TopicParser(mParserObserverProvider.getTopicObserver()));
        map.put(Commands.QUIT, new QuitParser(mParserObserverProvider.getQuitObserver()));

        return map;
    }

    @Override
    public Map<Integer, ReplyCodeParser> getReplyCodeParsers() {
        final HashMap<Integer, ReplyCodeParser> map = new HashMap<>();

        final NickPrefixNameParser nameParser = new NickPrefixNameParser(
                mParserObserverProvider.getNickPrefixNameObserver());
        map.put(ReplyCodes.RPL_NAMREPLY, nameParser);
        map.put(ReplyCodes.RPL_ENDOFNAMES, nameParser);

        return map;
    }
}