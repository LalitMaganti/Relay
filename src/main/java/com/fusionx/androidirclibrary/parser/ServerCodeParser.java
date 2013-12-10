/*
    HoloIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of HoloIRC.

    HoloIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HoloIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HoloIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.androidirclibrary.parser;

import com.fusionx.androidirclibrary.Channel;
import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.UserChannelInterface;
import com.fusionx.androidirclibrary.communication.ServerSenderBus;
import com.fusionx.androidirclibrary.event.ChannelEvent;
import com.fusionx.androidirclibrary.event.Event;
import com.fusionx.androidirclibrary.misc.InterfaceHolders;
import com.fusionx.androidirclibrary.util.IRCUtils;

import java.util.ArrayList;

import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.ERR_NICKNAMEINUSE;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_ENDOFMOTD;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_ENDOFNAMES;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_ENDOFWHO;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_MOTD;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_MOTDSTART;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_NAMREPLY;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_TOPIC;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_TOPICWHOTIME;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_WHOREPLY;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.doNothingCodes;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.genericCodes;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.whoisCodes;

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