package com.fusionx.relay.parser.code;

import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.constants.ServerReplyCodes;

import android.util.SparseArray;

import java.util.List;

import static com.fusionx.relay.constants.ServerReplyCodes.ERR_NICKNAMEINUSE;
import static com.fusionx.relay.constants.ServerReplyCodes.ERR_NOSUCHNICK;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_ENDOFMOTD;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_ENDOFNAMES;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_ENDOFWHO;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_MOTD;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_MOTDSTART;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_NAMREPLY;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_TOPIC;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_TOPICWHOTIME;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_WHOREPLY;

public abstract class CodeParser {

    protected final UserChannelInterface mUserChannelInterface;

    protected final Server mServer;

    protected final ServerEventBus mServerEventBus;

    CodeParser(final Server server) {
        mServer = server;
        mUserChannelInterface = server.getUserChannelInterface();
        mServerEventBus = server.getServerEventBus();
    }

    public static SparseArray<CodeParser> getParserMap(final Server server) {
        final SparseArray<CodeParser> parserMap = new SparseArray<>();

        final TopicParser parser = new TopicParser(server);
        parserMap.put(ServerReplyCodes.RPL_TOPIC, parser);
        parserMap.put(ServerReplyCodes.RPL_TOPICWHOTIME, parser);

        final NameParser nameParser = new NameParser(server);
        parserMap.put(RPL_NAMREPLY, nameParser);
        parserMap.put(RPL_ENDOFNAMES, nameParser);

        final MotdParser motdParser = new MotdParser(server);
        parserMap.put(RPL_MOTDSTART, motdParser);
        parserMap.put(RPL_MOTD, motdParser);
        parserMap.put(RPL_ENDOFMOTD, motdParser);

        final TopicParser topicParser = new TopicParser(server);
        parserMap.put(RPL_TOPIC, topicParser);
        parserMap.put(RPL_TOPICWHOTIME, topicParser);

        final WhoParser whoParser = new WhoParser(server);
        parserMap.put(RPL_WHOREPLY, whoParser);
        parserMap.put(RPL_ENDOFWHO, whoParser);

        final ErrorParser errorParser = new ErrorParser(server);
        parserMap.put(ERR_NOSUCHNICK, errorParser);
        parserMap.put(ERR_NICKNAMEINUSE, errorParser);

        return parserMap;
    }

    public abstract void onParseCode(final int code, final List<String> parsedArray);
}