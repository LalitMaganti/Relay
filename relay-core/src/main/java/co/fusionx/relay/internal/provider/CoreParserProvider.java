package co.fusionx.relay.internal.provider;

import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.constant.ReplyCodes;
import co.fusionx.relay.internal.constants.Commands;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ParserProvider;
import co.fusionx.relay.parser.ReplyCodeParser;
import co.fusionx.relay.parser.ircv3.CapParser;
import co.fusionx.relay.parser.ircv3.NickPrefixNameParser;
import co.fusionx.relay.parser.ircv3.SaslParser;
import co.fusionx.relay.parser.rfc.InviteParser;
import co.fusionx.relay.parser.rfc.JoinParser;
import co.fusionx.relay.parser.rfc.KickParser;
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

        // RFC
        map.put(Commands.INVITE,
                new InviteParser().addObservers(mParserObserverProvider.getInviteObservers()));
        map.put(Commands.JOIN,
                new JoinParser().addObservers(mParserObserverProvider.getJoinObservers()));
        map.put(Commands.KICK,
                new KickParser().addObservers(mParserObserverProvider.getKickObservers()));
        map.put(Commands.NICK,
                new NickParser().addObservers(mParserObserverProvider.getNickObservers()));
        map.put(Commands.PART,
                new PartParser().addObservers(mParserObserverProvider.getPartObservers()));
        map.put(Commands.PING,
                new PingParser().addObservers(mParserObserverProvider.getPingObservers()));
        map.put(Commands.TOPIC,
                new TopicParser().addObservers(mParserObserverProvider.getTopicObservers()));
        map.put(Commands.QUIT,
                new QuitParser().addObservers(mParserObserverProvider.getQuitObservers()));

        // IRCv3
        map.put(Commands.CAP,
                new CapParser().addObservers(mParserObserverProvider.getCapObservers()));
        map.put(Commands.AUTHENTICATE,
                new SaslParser().addObservers(mParserObserverProvider.getSaslObservers()));

        return map;
    }

    @Override
    public Map<Integer, ReplyCodeParser> getReplyCodeParsers() {
        final HashMap<Integer, ReplyCodeParser> map = new HashMap<>();

        final MotdParser motdParser = new MotdParser()
                .addObservers(mParserObserverProvider.getMotdObservers());
        map.put(ReplyCodes.RPL_MOTDSTART, motdParser);
        map.put(ReplyCodes.RPL_MOTD, motdParser);
        map.put(ReplyCodes.RPL_ENDOFMOTD, motdParser);

        final NickPrefixNameParser nameParser = new NickPrefixNameParser()
                .addObservers(mParserObserverProvider.getNickPrefixNameObservers());
        map.put(ReplyCodes.RPL_NAMREPLY, nameParser);
        map.put(ReplyCodes.RPL_ENDOFNAMES, nameParser);

        final TopicCodeParser topicCodeParser = new TopicCodeParser()
                .addObservers(mParserObserverProvider.getTopicCodeObservers());
        map.put(ReplyCodes.RPL_TOPIC, topicCodeParser);
        map.put(ReplyCodes.RPL_TOPICWHOTIME, topicCodeParser);

        return map;
    }
}