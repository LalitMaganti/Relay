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
import com.fusionx.androidirclibrary.util.IRCUtils;

import java.util.ArrayList;

/**
 * This entire class needs full parsing
 */
public class UserInputParser {

    public static void channelMessageToParse(final Server server,
            final String channelName, final String message, final ParserCallbacks callbacks) {
        final ArrayList<String> parsedArray = IRCUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);

        if (command.startsWith("/")) {
            if (command.equals("/me")) {
                final String action = IRCUtils.convertArrayListToString(parsedArray);
                server.getServerReceiverBus().sendActionToChannel(channelName, action);
            } else if (command.equals("/part") || command.equals("/p")) {
                if (parsedArray.size() == 0) {
                    server.getServerReceiverBus().sendPart(channelName);
                } else {
                    sendUnknownEvent(server, message);
                }
            } else if (command.equals("/mode")) {
                if (parsedArray.size() == 2) {
                    server.getServerReceiverBus().sendMode(channelName, parsedArray.get(0),
                            parsedArray.get(1));
                } else {
                    sendUnknownEvent(server, message);
                }
            } else {
                serverCommandToParse(server, message, callbacks);
            }
        } else {
            server.getServerReceiverBus().sendMessageToChannel(channelName, message);
        }
    }

    public static void userMessageToParse(final Server server,
            final String userNick, final String message, final ParserCallbacks callbacks) {
        final ArrayList<String> parsedArray = IRCUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);

        if (command.startsWith("/")) {
            if (command.equals("/me")) {
                final String action = IRCUtils.convertArrayListToString(parsedArray);
                server.getServerReceiverBus().sendActionToUser(userNick, action);
            } else if (command.equals("/close") || command.equals("/c")) {
                if (parsedArray.size() == 0) {
                    server.getServerReceiverBus().sendClosePrivateMessage(server
                            .getPrivateMessageUser(userNick));
                } else {
                    sendUnknownEvent(server, message);
                }
            } else {
                serverCommandToParse(server, message, callbacks);
            }
        } else {
            server.getServerReceiverBus().sendMessageToUser(userNick, message);
        }
    }

    public static void serverMessageToParse(final Server server,
            final String message, final ParserCallbacks callbacks) {
        if (message.startsWith("/")) {
            serverCommandToParse(server, message, callbacks);
        } else {
            sendUnknownEvent(server, message);
        }
    }

    private static void serverCommandToParse(final Server server,
            final String rawLine, final ParserCallbacks callbacks) {
        final ArrayList<String> parsedArray = IRCUtils.splitRawLine(rawLine, false);
        final String command = parsedArray.remove(0);

        if (command.equals("/join") || command.equals("/j")) {
            if (parsedArray.size() == 1) {
                final String channelName = parsedArray.get(0);
                server.getServerReceiverBus().sendJoin(channelName);
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/msg")) {
            if (parsedArray.size() >= 1) {
                final String nick = parsedArray.remove(0);
                if (parsedArray.size() >= 1) {
                    final String message = IRCUtils.convertArrayListToString(parsedArray);
                    server.getServerReceiverBus().sendMessageToUser(nick, message);
                } else {
                    callbacks.openPrivateMessage(nick);
                }
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/nick")) {
            if (parsedArray.size() == 1) {
                final String newNick = parsedArray.get(0);
                server.getServerReceiverBus().sendNickChange(newNick);
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/quit")) {
            if (parsedArray.size() == 0) {
                server.getServerReceiverBus().sendDisconnect();
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/whois")) {
            if (parsedArray.size() == 1) {
                server.getServerReceiverBus().sendUserWhois(parsedArray.get(0));
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/raw")) {
            server.getServerReceiverBus().sendRawLine(IRCUtils.convertArrayListToString
                    (parsedArray));
        } else if (command.startsWith("/")) {
            server.getServerReceiverBus().sendRawLine(command.substring(1) + IRCUtils
                    .convertArrayListToString(parsedArray));
        } else {
            sendUnknownEvent(server, rawLine);
        }
    }

    private static void sendUnknownEvent(final Server server, final String rawLine) {
        server.getServerReceiverBus().sendUnknownEvent(rawLine + " is not a valid command");
    }

    public interface ParserCallbacks {

        public void openPrivateMessage(final String nick);
    }
}