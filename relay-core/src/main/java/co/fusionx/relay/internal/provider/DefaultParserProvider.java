package co.fusionx.relay.internal.provider;

import java.util.HashMap;
import java.util.Map;

import co.fusionx.relay.internal.constants.Commands;
import co.fusionx.relay.parser.CommandParser;
import co.fusionx.relay.parser.ParserProvider;
import co.fusionx.relay.parser.ReplyCodeParser;
import co.fusionx.relay.parser.ctcp.CTCPCommandParser;
import co.fusionx.relay.parser.ctcp.CTCPReplyParser;
import co.fusionx.relay.parser.ircv3.CapParser;
import co.fusionx.relay.parser.ircv3.NickPrefixNameParser;
import co.fusionx.relay.parser.ircv3.SaslParser;
import co.fusionx.relay.parser.rfc.InviteParser;
import co.fusionx.relay.parser.rfc.JoinParser;
import co.fusionx.relay.parser.rfc.KickParser;
import co.fusionx.relay.parser.rfc.ModeParser;
import co.fusionx.relay.parser.rfc.MotdParser;
import co.fusionx.relay.parser.rfc.NickParser;
import co.fusionx.relay.parser.rfc.NoticeParser;
import co.fusionx.relay.parser.rfc.PartParser;
import co.fusionx.relay.parser.rfc.PingParser;
import co.fusionx.relay.parser.rfc.PrivmsgParser;
import co.fusionx.relay.parser.rfc.QuitParser;
import co.fusionx.relay.parser.rfc.TopicCodeParser;
import co.fusionx.relay.parser.rfc.TopicParser;
import co.fusionx.relay.parser.rfc.WallopsParser;
import co.fusionx.relay.parser.rfc.WelcomeParser;

public class DefaultParserProvider implements ParserProvider {

    private final ParserObserverProvider mParserObserverProvider;

    private final SaslParser mSaslParser;

    public DefaultParserProvider(final ParserObserverProvider parserObserverProvider) {
        mParserObserverProvider = parserObserverProvider;

        mSaslParser = new SaslParser().addObservers(parserObserverProvider.getSaslObservers());
    }

    @Override
    public Map<String, CommandParser> getCommandParsers() {
        final HashMap<String, CommandParser> map = new HashMap<>();

        final NoticeParser noticeParser = new NoticeParser()
                .addObservers(mParserObserverProvider.getNoticeObservers());
        final PrivmsgParser privmsgParser = new PrivmsgParser()
                .addObservers(mParserObserverProvider.getPrivMsgObservers());

        // Add RFC command parsers
        map.put(Commands.INVITE,
                new InviteParser().addObservers(mParserObserverProvider.getInviteObservers()));
        map.put(Commands.JOIN,
                new JoinParser().addObservers(mParserObserverProvider.getJoinObservers()));
        map.put(Commands.KICK,
                new KickParser().addObservers(mParserObserverProvider.getKickObservers()));
        map.put(Commands.MODE,
                new ModeParser().addObservers(mParserObserverProvider.getModeObservers()));
        map.put(Commands.NICK,
                new NickParser().addObservers(mParserObserverProvider.getNickObservers()));
        map.put(Commands.NOTICE, noticeParser);
        map.put(Commands.PART,
                new PartParser().addObservers(mParserObserverProvider.getPartObservers()));
        map.put(Commands.PING,
                new PingParser().addObservers(mParserObserverProvider.getPingObservers()));
        map.put(Commands.PRIVMSG, privmsgParser);
        map.put(Commands.QUIT,
                new QuitParser().addObservers(mParserObserverProvider.getQuitObservers()));
        map.put(Commands.TOPIC,
                new TopicParser().addObservers(mParserObserverProvider.getTopicObservers()));
        map.put(Commands.WALLOPS,
                new WallopsParser().addObservers(mParserObserverProvider.getWallopsObservers()));

        // Add CTCP observers
        final CTCPReplyParser ctcpReplyParser = new CTCPReplyParser();
        noticeParser.addObserver(ctcpReplyParser);

        final CTCPCommandParser ctcpCommandParser = new CTCPCommandParser();
        privmsgParser.addObserver(ctcpCommandParser);

        // Add IRCv3 parsers
        map.put(Commands.CAP,
                new CapParser().addObservers(mParserObserverProvider.getCapObservers()));
        map.put(Commands.AUTHENTICATE, mSaslParser);

        return map;
    }

    @Override
    public Map<Integer, ReplyCodeParser> getReplyCodeParsers() {
        final Map<Integer, ReplyCodeParser> map = new HashMap<>();

        // RFC Reply Parsers
        final MotdParser motdParser = new MotdParser()
                .addObservers(mParserObserverProvider.getMotdObservers());
        addCodeParsers(map, motdParser);

        final NickPrefixNameParser nameParser = new NickPrefixNameParser()
                .addObservers(mParserObserverProvider.getNickPrefixNameObservers());
        addCodeParsers(map, nameParser);

        final TopicCodeParser topicCodeParser = new TopicCodeParser()
                .addObservers(mParserObserverProvider.getTopicCodeObservers());
        addCodeParsers(map, topicCodeParser);

        final WelcomeParser welcomeParser = new WelcomeParser()
                .addObservers(mParserObserverProvider.getWelcomeObservers());
        addCodeParsers(map, welcomeParser);

        // IRCv3 Parsers
        addCodeParsers(map, mSaslParser);

        return map;
    }

    private void addCodeParsers(final Map<Integer, ReplyCodeParser> map,
            final ReplyCodeParser parser) {
        for (final int codes : parser.parsableCodes()) {
            map.put(codes, parser);
        }
    }
}