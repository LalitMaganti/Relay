package com.fusionx.relay.parser;

import com.fusionx.relay.Channel;
import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.communication.ServerSenderBus;
import com.fusionx.relay.event.ChannelEvent;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.IRCUtils;

import java.util.ArrayList;

import static com.fusionx.relay.constants.ServerReplyCodes.ERR_NICKNAMEINUSE;
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

class ServerCodeParser {

    private final WhoParser mWhoParser;

    private final NameParser mNameParser;

    private final UserChannelInterface mUserChannelInterface;

    private final Server mServer;

    private final ServerSenderBus mServerSenderBus;

    ServerCodeParser(final ServerLineParser parser) {
        mServer = parser.getServer();
        mUserChannelInterface = mServer.getUserChannelInterface();
        mWhoParser = new WhoParser(mUserChannelInterface, mServer);
        mNameParser = new NameParser(mUserChannelInterface, mServer);
        mServerSenderBus = mServer.getServerSenderBus();
    }

    /**
     * The server is sending a code to us - parse what it is
     *
     * @param parsedArray - the array of the line (split by spaces)
     */
    Event parseCode(final ArrayList<String> parsedArray, final String rawLine) {
        final int code = Integer.parseInt(parsedArray.get(1));

        // Pretty common across all the codes
        IRCUtils.removeFirstElementFromList(parsedArray, 3);
        final String message = parsedArray.get(0);

        switch (code) {
            case RPL_NAMREPLY:
                return mNameParser.parseNameReply(parsedArray);
            case RPL_ENDOFNAMES:
                return mNameParser.parseNameFinished();
            case RPL_MOTDSTART:
            case RPL_MOTD:
                final String motdline = message.substring(1).trim();
                //if (AppPreferences.motdAllowed) {
                return mServerSenderBus.sendGenericServerEvent(mServer, motdline);
            //} else {
            //    return new Event(motdline);
            //}
            case RPL_ENDOFMOTD:
                //if (AppPreferences.motdAllowed) {
                return mServerSenderBus.sendGenericServerEvent(mServer, message);
            //} else {
            //    return new Event(message);
            //}
            case RPL_TOPIC:
                return parseTopicReply(parsedArray);
            case RPL_TOPICWHOTIME:
                return parseTopicInfo(parsedArray);
            case RPL_WHOREPLY:
                return mWhoParser.parseWhoReply(parsedArray);
            case RPL_ENDOFWHO:
                return mWhoParser.parseWhoFinished();
            case ERR_NICKNAMEINUSE:
                return mServerSenderBus.sendNickInUseMessage(mServer);
            default:
                return parseFallThroughCode(code, message, parsedArray);
        }
    }

    private Event parseTopicReply(ArrayList<String> parsedArray) {
        final String topic = parsedArray.get(1);
        final Channel channel = mUserChannelInterface.getChannel(parsedArray.get(0));
        channel.setTopic(topic);
        return new Event(topic);
    }

    // TODO - maybe using a colorful nick here if available?
    // TODO - possible optimization - make a new parser for topic stuff
    // Allows reduced overhead of retrieving channel from interface
    private ChannelEvent parseTopicInfo(final ArrayList<String> parsedArray) {
        final String channelName = parsedArray.get(0);
        final String nick = IRCUtils.getNickFromRaw(parsedArray.get(1));
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        final String eventMessage = InterfaceHolders.getEventResponses().getInitialTopicMessage
                (channel.getTopic(), nick);

        return mServerSenderBus.sendGenericChannelEvent(channel, eventMessage, false);
    }

    private Event parseFallThroughCode(final int code, final String message,
            final ArrayList<String> parsedArray) {
        if (genericCodes.contains(code)) {
            return mServerSenderBus.sendGenericServerEvent(mServer, message);
        } else if (whoisCodes.contains(code)) {
            return mServerSenderBus
                    .sendSwitchToServerEvent(mServer, IRCUtils.convertArrayListToString
                            (parsedArray));
        } else if (doNothingCodes.contains(code)) {
            return new Event(message);
        }
        return new Event(message);
    }
}