package co.fusionx.relay.internal.base;

import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Singleton;

import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.constants.CapCapability;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.constants.CommandConstants;
import co.fusionx.relay.internal.constants.ServerReplyCodes;
import co.fusionx.relay.internal.parser.main.code.CodeParser;
import co.fusionx.relay.internal.parser.main.code.ErrorParser;
import co.fusionx.relay.internal.parser.main.code.InitalTopicParser;
import co.fusionx.relay.internal.parser.main.code.MotdParser;
import co.fusionx.relay.internal.parser.main.code.NameParser;
import co.fusionx.relay.internal.parser.main.command.AccountParser;
import co.fusionx.relay.internal.parser.main.command.AwayParser;
import co.fusionx.relay.internal.parser.main.command.CTCPParser;
import co.fusionx.relay.internal.parser.main.command.CommandParser;
import co.fusionx.relay.internal.parser.main.command.DCCParser;
import co.fusionx.relay.internal.parser.main.command.ErrorCommandParser;
import co.fusionx.relay.internal.parser.main.command.InviteParser;
import co.fusionx.relay.internal.parser.main.command.JoinParser;
import co.fusionx.relay.internal.parser.main.command.KickParser;
import co.fusionx.relay.internal.parser.main.command.ModeParser;
import co.fusionx.relay.internal.parser.main.command.NickParser;
import co.fusionx.relay.internal.parser.main.command.NoticeParser;
import co.fusionx.relay.internal.parser.main.command.PartParser;
import co.fusionx.relay.internal.parser.main.command.PingParser;
import co.fusionx.relay.internal.parser.main.command.PongParser;
import co.fusionx.relay.internal.parser.main.command.PrivmsgParser;
import co.fusionx.relay.internal.parser.main.command.QuitParser;
import co.fusionx.relay.internal.parser.main.command.TopicChangeParser;
import co.fusionx.relay.internal.parser.main.command.WallopsParser;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.internal.sender.RelayBaseSender;
import co.fusionx.relay.internal.sender.RelayServerSender;
import co.fusionx.relay.misc.EventBus;
import co.fusionx.relay.sender.ServerSender;
import dagger.Module;
import dagger.Provides;

import static co.fusionx.relay.internal.constants.CommandConstants.ERROR;
import static co.fusionx.relay.internal.constants.CommandConstants.INVITE;
import static co.fusionx.relay.internal.constants.CommandConstants.JOIN;
import static co.fusionx.relay.internal.constants.CommandConstants.KICK;
import static co.fusionx.relay.internal.constants.CommandConstants.MODE;
import static co.fusionx.relay.internal.constants.CommandConstants.NICK;
import static co.fusionx.relay.internal.constants.CommandConstants.NOTICE;
import static co.fusionx.relay.internal.constants.CommandConstants.PART;
import static co.fusionx.relay.internal.constants.CommandConstants.PING;
import static co.fusionx.relay.internal.constants.CommandConstants.PONG;
import static co.fusionx.relay.internal.constants.CommandConstants.PRIVMSG;
import static co.fusionx.relay.internal.constants.CommandConstants.QUIT;
import static co.fusionx.relay.internal.constants.CommandConstants.TOPIC;
import static co.fusionx.relay.internal.constants.CommandConstants.WALLOPS;

@Module(injects = {
        RelaySession.class, RelayIRCConnection.class, RelayServer.class, RelayUserChannelDao.class
})
public class RelayBaseModule {

    private final ServerConfiguration mConfiguration;

    public RelayBaseModule(final ServerConfiguration serverConfiguration) {
        mConfiguration = serverConfiguration;
    }

    @Provides
    public ServerConfiguration provideConfiguration() {
        return mConfiguration;
    }

    @Provides
    @Singleton
    public StatusManager provideStatusManager(final ServerConfiguration configuration,
            final RelayServer server, final RelayUserChannelDao dao) {
        return new RelayStatusManager(configuration, server, dao);
    }

    @Provides
    @Singleton
    public ScheduledExecutorService provideMainExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Singleton
    @Provides
    public BaseSender provideBaseSender() {
        return new RelayBaseSender();
    }

    @Singleton
    @Provides
    public EventBus<Event> provideServerWideEventBus() {
        return new EventBus<>();
    }

    @Provides
    public ServerSender provideServerSender(final BaseSender sender) {
        return new RelayServerSender(sender);
    }

    @Provides
    @Singleton
    public Map<String, CommandParser> provideCommandParserMap(final RelayServer server,
            final RelayUserChannelDao dao, final BaseSender sender) {
        final DCCParser dccParser = new DCCParser(server);
        final CTCPParser ctcpParser = new CTCPParser(server, dao, sender, dccParser);

        final Map<String, CommandParser> parserMap = new HashMap<>();

        // Core RFC parsers
        parserMap.put(PING, new PingParser(server, dao, sender));
        parserMap.put(ERROR, new ErrorCommandParser(server, dao));

        // RFC parsers
        parserMap.put(JOIN, new JoinParser(server, dao));
        parserMap.put(PRIVMSG, new PrivmsgParser(server, dao, ctcpParser));
        parserMap.put(NOTICE, new NoticeParser(server, dao, ctcpParser));
        parserMap.put(PART, new PartParser(server, dao));
        parserMap.put(MODE, new ModeParser(server, dao));
        parserMap.put(QUIT, new QuitParser(server, dao));
        parserMap.put(NICK, new NickParser(server, dao));
        parserMap.put(TOPIC, new TopicChangeParser(server, dao));
        parserMap.put(KICK, new KickParser(server, dao));
        parserMap.put(INVITE, new InviteParser(server, dao));
        parserMap.put(PONG, new PongParser(server, dao));
        parserMap.put(WALLOPS, new WallopsParser(server, dao));

        // Optional IRCv3 parsers
        if (server.getCapabilities().contains(CapCapability.ACCOUNTNOTIFY)) {
            parserMap.put(CommandConstants.ACCOUNT, new AccountParser(server, dao));
        }
        if (server.getCapabilities().contains(CapCapability.AWAYNOTIFY)) {
            parserMap.put(CommandConstants.AWAY, new AwayParser(server, dao));
        }

        return parserMap;
    }

    @Provides
    @Singleton
    public SparseArray<CodeParser> provideCodeParserMap(final EventBus<Event> superBus,
            final RelayServer server, final RelayUserChannelDao dao, final BaseSender sender) {
        final SparseArray<CodeParser> parserMap = new SparseArray<>();

        final InitalTopicParser topicChangeParser = new InitalTopicParser(server, dao);
        parserMap.put(ServerReplyCodes.RPL_TOPIC, topicChangeParser);
        parserMap.put(ServerReplyCodes.RPL_TOPICWHOTIME, topicChangeParser);

        final NameParser nameParser = new NameParser(server, dao);
        parserMap.put(ServerReplyCodes.RPL_NAMREPLY, nameParser);
        parserMap.put(ServerReplyCodes.RPL_ENDOFNAMES, nameParser);

        final MotdParser motdParser = new MotdParser(server, dao);
        parserMap.put(ServerReplyCodes.RPL_MOTDSTART, motdParser);
        parserMap.put(ServerReplyCodes.RPL_MOTD, motdParser);
        parserMap.put(ServerReplyCodes.RPL_ENDOFMOTD, motdParser);

        final ErrorParser errorParser = new ErrorParser(server, dao);
        parserMap.put(ServerReplyCodes.ERR_NOSUCHNICK, errorParser);
        parserMap.put(ServerReplyCodes.ERR_NICKNAMEINUSE, errorParser);

        return parserMap;
    }
}