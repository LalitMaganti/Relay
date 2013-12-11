package com.fusionx.relay.parser;

import com.fusionx.relay.Server;
import com.fusionx.relay.util.IRCUtils;

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
                server.getServerCallBus().sendActionToChannel(channelName, action);
            } else if (command.equals("/part") || command.equals("/p")) {
                if (parsedArray.size() == 0) {
                    server.getServerCallBus().sendPart(channelName);
                } else {
                    sendUnknownEvent(server, message);
                }
            } else if (command.equals("/mode")) {
                if (parsedArray.size() == 2) {
                    server.getServerCallBus().sendMode(channelName, parsedArray.get(0),
                            parsedArray.get(1));
                } else {
                    sendUnknownEvent(server, message);
                }
            } else {
                serverCommandToParse(server, message, callbacks);
            }
        } else {
            server.getServerCallBus().sendMessageToChannel(channelName, message);
        }
    }

    public static void userMessageToParse(final Server server,
            final String userNick, final String message, final ParserCallbacks callbacks) {
        final ArrayList<String> parsedArray = IRCUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);

        if (command.startsWith("/")) {
            if (command.equals("/me")) {
                final String action = IRCUtils.convertArrayListToString(parsedArray);
                server.getServerCallBus().sendActionToUser(userNick, action);
            } else if (command.equals("/close") || command.equals("/c")) {
                if (parsedArray.size() == 0) {
                    server.getServerCallBus().sendClosePrivateMessage(server
                            .getPrivateMessageUser(userNick));
                } else {
                    sendUnknownEvent(server, message);
                }
            } else {
                serverCommandToParse(server, message, callbacks);
            }
        } else {
            server.getServerCallBus().sendMessageToUser(userNick, message);
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
                server.getServerCallBus().sendJoin(channelName);
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/msg")) {
            if (parsedArray.size() >= 1) {
                final String nick = parsedArray.remove(0);
                if (parsedArray.size() >= 1) {
                    final String message = IRCUtils.convertArrayListToString(parsedArray);
                    server.getServerCallBus().sendMessageToUser(nick, message);
                } else {
                    callbacks.openPrivateMessage(nick);
                }
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/nick")) {
            if (parsedArray.size() == 1) {
                final String newNick = parsedArray.get(0);
                server.getServerCallBus().sendNickChange(newNick);
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/quit")) {
            if (parsedArray.size() == 0) {
                server.getServerCallBus().sendDisconnect();
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/whois")) {
            if (parsedArray.size() == 1) {
                server.getServerCallBus().sendUserWhois(parsedArray.get(0));
            } else {
                sendUnknownEvent(server, rawLine);
            }
        } else if (command.equals("/raw")) {
            server.getServerCallBus().sendRawLine(IRCUtils.convertArrayListToString
                    (parsedArray));
        } else if (command.startsWith("/")) {
            server.getServerCallBus().sendRawLine(command.substring(1) + IRCUtils
                    .convertArrayListToString(parsedArray));
        } else {
            sendUnknownEvent(server, rawLine);
        }
    }

    private static void sendUnknownEvent(final Server server, final String rawLine) {
        server.getServerCallBus().sendUnknownEvent(rawLine + " is not a valid command");
    }

    public interface ParserCallbacks {

        public void openPrivateMessage(final String nick);
    }
}