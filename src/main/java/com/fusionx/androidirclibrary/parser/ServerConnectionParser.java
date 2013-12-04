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

import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.ServerConfiguration;
import com.fusionx.androidirclibrary.communication.MessageSender;
import com.fusionx.androidirclibrary.constants.ServerCommands;
import com.fusionx.androidirclibrary.misc.CoreListener;
import com.fusionx.androidirclibrary.util.IRCUtils;
import com.fusionx.androidirclibrary.writers.ServerWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.ERR_NICKNAMEINUSE;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.ERR_NONICKNAMEGIVEN;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.RPL_WELCOME;
import static com.fusionx.androidirclibrary.constants.ServerReplyCodes.saslCodes;

public class ServerConnectionParser {

    private static boolean triedSecondNick = false;

    private static boolean triedThirdNick = false;

    private static int suffix = 0;

    public static String parseConnect(final Server server, final ServerConfiguration
            configuration, final BufferedReader reader) throws IOException {

        String line;
        suffix = 0;
        triedSecondNick = false;
        triedThirdNick = false;
        final MessageSender sender = MessageSender.getSender(server.getTitle());

        while ((line = reader.readLine()) != null) {
            final ArrayList<String> parsedArray = IRCUtils.splitRawLine(line, true);
            String s = parsedArray.get(0);
            if (s.equals(ServerCommands.Ping)) {// Immediately return
                final String source = parsedArray.get(1);
                CoreListener.respondToPing(server.getWriter(), source);
            } else if (s.equals(ServerCommands.Error)) {// We are finished - the server has
                // kicked us out for some reason
                return null;
            } else if (s.equals(ServerCommands.Authenticate)) {
                CapParser.parseCommand(parsedArray, configuration, server, sender);
            } else {
                if (StringUtils.isNumeric(parsedArray.get(1))) {
                    final String nick = parseConnectionCode(configuration.isNickChangable(),
                            parsedArray, sender, server,
                            configuration.getNickStorage(), line);
                    if (nick != null) {
                        return nick;
                    }
                } else {
                    parseConnectionCommand(parsedArray, configuration, sender,
                            server, line);
                }
            }
        }
        return null;
    }

    private static String parseConnectionCode(final boolean canChangeNick,
            final ArrayList<String> parsedArray,
            final MessageSender sender,
            final Server server,
            final ServerConfiguration.NickStorage nickStorage,
            final String rawLine) {
        final int code = Integer.parseInt(parsedArray.get(1));
        final ServerWriter writer = server.getWriter();
        switch (code) {
            case RPL_WELCOME:
                // We are now logged in.
                final String nick = parsedArray.get(2);
                IRCUtils.removeFirstElementFromList(parsedArray, 3);
                return nick;
            case ERR_NICKNAMEINUSE:
                if (!triedSecondNick && StringUtils.isNotEmpty(nickStorage.getSecondChoiceNick())) {
                    writer.changeNick(nickStorage.getSecondChoiceNick());
                    triedSecondNick = true;
                } else if (!triedThirdNick && StringUtils.isNotEmpty(nickStorage
                        .getThirdChoiceNick())) {
                    writer.changeNick(nickStorage.getThirdChoiceNick());
                    triedThirdNick = true;
                } else {
                    if (canChangeNick) {
                        ++suffix;
                        writer.changeNick(nickStorage.getFirstChoiceNick() + suffix);
                    } else {
                        sender.sendNickInUseMessage(server);
                    }
                }
                break;
            case ERR_NONICKNAMEGIVEN:
                writer.changeNick(nickStorage.getFirstChoiceNick());
                break;
            default:
                if (saslCodes.contains(code)) {
                    CapParser.parseCode(code, parsedArray, sender, server);
                    //} else if (DEBUG) {
                    //    Log.v(LOG_TAG, rawLine);
                }
                break;
        }
        return null;
    }

    private static void parseConnectionCommand(final ArrayList<String> parsedArray,
            final ServerConfiguration configuration,
            final MessageSender sender, final Server server,
            final String rawLine) {
        String s = parsedArray.get(1).toUpperCase();
        if (s.equals(ServerCommands.Notice)) {
            IRCUtils.removeFirstElementFromList(parsedArray, 3);
            sender.sendGenericServerEvent(server, parsedArray.get(0));
        } else if (s.equals(ServerCommands.Cap)) {
            IRCUtils.removeFirstElementFromList(parsedArray, 3);
            CapParser.parseCommand(parsedArray, configuration, server, sender);
            //} else  (DEBUG) {
            //        Log.v(LOG_TAG, rawLine);
        }
    }

    /**
     * Not to be instantiated
     */
    private ServerConnectionParser() {
    }
}
