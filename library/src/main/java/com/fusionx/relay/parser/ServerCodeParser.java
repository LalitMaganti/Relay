package com.fusionx.relay.parser;

import com.fusionx.relay.Channel;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.parser.code.NameParser;
import com.fusionx.relay.parser.code.WhoParser;
import com.fusionx.relay.util.IRCUtils;

import java.util.ArrayList;

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
import static com.fusionx.relay.constants.ServerReplyCodes.doNothingCodes;
import static com.fusionx.relay.constants.ServerReplyCodes.genericCodes;
import static com.fusionx.relay.constants.ServerReplyCodes.whoisCodes;
/*
class ServerCodeParser {

    private final WhoParser mWhoParser;

    private final NameParser mNameParser;

    private final UserChannelInterface mUserChannelInterface;

    private final Server mServer;

    private final ServerEventBus mServerEventBus;

    ServerCodeParser(final ServerLineParser parser) {
        mServer = parser.getServer();
        mUserChannelInterface = mServer.getUserChannelInterface();
        mServerEventBus = mServer.getServerEventBus();

        mWhoParser = new WhoParser(mUserChannelInterface, mServer);
        mNameParser = new NameParser(mUserChannelInterface, mServer);
    }

    /**
     * The server is sending a code to us - parse what it is
     *
     * @param parsedArray - the array of the line (split by spaces)

    void onParseCode(final ArrayList<String> parsedArray, final String rawLine) {
        final int code = Integer.parseInt(parsedArray.get(1));

        // Pretty common across all the codes
        IRCUtils.removeFirstElementFromList(parsedArray, 3);
        final String message = parsedArray.get(0);

        switch (code) {
            case RPL_NAMREPLY:
                mNameParser.parseNameReply(parsedArray);
                break;
            case RPL_ENDOFNAMES:
                mNameParser.parseNameFinished();
                break;
            case RPL_MOTDSTART:
            case RPL_MOTD:
                final String motdline = message.substring(1).trim();
                if (InterfaceHolders.getPreferences().isMOTDShown()) {
                    mServerEventBus.sendGenericServerEvent(motdline);
                }
                break;
            case RPL_ENDOFMOTD:
                if (InterfaceHolders.getPreferences().isMOTDShown()) {
                    mServerEventBus.sendGenericServerEvent(message);
                }
                break;
            case RPL_TOPIC:
                onTopic(parsedArray);
                break;
            case RPL_TOPICWHOTIME:
                onTopicInfo(parsedArray);
                break;
            case RPL_WHOREPLY:
                mWhoParser.parseWhoReply(parsedArray);
                break;
            case RPL_ENDOFWHO:
                mWhoParser.parseWhoFinished();
                break;
            case ERR_NOSUCHNICK:
                // Should only occur when we send a PM to someone who has changed their nick or
                // has quit the server
                onNoSuchNickError(parsedArray);
                break;
            case ERR_NICKNAMEINUSE:
                mServerEventBus.sendNickInUseMessage();
                break;
            default:
                onFallThrough(code, message, parsedArray);
                break;
        }
    }

    private void onFallThrough(final int code, final String message,
            final ArrayList<String> parsedArray) {
        if (genericCodes.contains(code)) {
            mServerEventBus.sendGenericServerEvent(message);
        } else if (whoisCodes.contains(code)) {
            final String response = IRCUtils.concatStringList(parsedArray);
            mServerEventBus.sendSwitchToServerEvent(response);
        } else if (doNothingCodes.contains(code)) {
            // Do nothing
        }
    }
}*/