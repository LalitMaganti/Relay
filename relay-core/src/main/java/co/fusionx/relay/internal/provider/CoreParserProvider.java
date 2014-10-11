package co.fusionx.relay.internal.provider;

import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.internal.constants.Commands;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ParserProvider;
import co.fusionx.relay.parser.ReplyCodeParser;
import co.fusionx.relay.parser.ircv3.NickPrefixNameParser;
import co.fusionx.relay.parser.rfc.InviteParser;
import co.fusionx.relay.parser.rfc.JoinParser;
import co.fusionx.relay.parser.rfc.MotdParser;
import co.fusionx.relay.parser.rfc.NickParser;
import co.fusionx.relay.parser.rfc.PartParser;
import co.fusionx.relay.parser.rfc.PingParser;
import co.fusionx.relay.parser.rfc.QuitParser;
import co.fusionx.relay.parser.rfc.TopicCodeParser;
import co.fusionx.relay.parser.rfc.TopicParser;

public class CoreParserProvider implements ParserProvider {

    private final ParserObserverProvider mParserObserverProvider;

    public CoreParserProvider(final ParserObserverProvider parserObserverProvider) {
        mParserObserverProvider = parserObserverProvider;
    }

    @Override
    public Map<String, CommandParser> getCommandParsers() {
        final HashMap<String, CommandParser> map = new HashMap<>();

        map.put(Commands.INVITE, new InviteParser(mParserObserverProvider.getInviteObserver()));
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

        final MotdParser motdParser = new MotdParser(mParserObserverProvider.getMotdObserver());
        map.put(ReplyCodes.RPL_MOTDSTART, motdParser);
        map.put(ReplyCodes.RPL_MOTD, motdParser);
        map.put(ReplyCodes.RPL_ENDOFMOTD, motdParser);

        final NickPrefixNameParser nameParser = new NickPrefixNameParser(
                mParserObserverProvider.getNickPrefixNameObserver());
        map.put(ReplyCodes.RPL_NAMREPLY, nameParser);
        map.put(ReplyCodes.RPL_ENDOFNAMES, nameParser);

        final TopicCodeParser topicCodeParser = new TopicCodeParser(mParserObserverProvider
                .getTopicCodeObserver());
        map.put(ReplyCodes.RPL_TOPIC, topicCodeParser);
        map.put(ReplyCodes.RPL_TOPICWHOTIME, topicCodeParser);

        return map;
    }
}