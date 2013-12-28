package com.fusionx.relay.parser;

import com.fusionx.relay.Server;
import com.fusionx.relay.util.IRCUtils;

import java.util.ArrayList;

public class UserInputParser {

    public static void onParseChannelMessage(final Server server, final String channelName,
            final String message) {
        final ArrayList<String> parsedArray = IRCUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        if (command.startsWith("/")) {
            switch (command) {
                case "/me":
                    final String action = IRCUtils.convertArrayListToString(parsedArray);
                    server.getServerCallBus().sendActionToChannel(channelName, action);
                    return;
                case "/part":
                case "/p":
                    if (arrayLength == 0) {
                        server.getServerCallBus().sendPart(channelName);
                        return;
                    }
                    break;
                case "/mode":
                    if (arrayLength == 2) {
                        server.getServerCallBus().sendMode(channelName, parsedArray.get(0),
                                parsedArray.get(1));
                        return;
                    }
                    break;
                case "/kick":
                    if (arrayLength >= 1) {
                        final String nick = parsedArray.remove(0);
                        final String reason = parsedArray.size() >= 1 ? IRCUtils
                                .convertArrayListToString(parsedArray) : "";
                        server.getServerCallBus().sendKick(channelName, nick, reason);
                        return;
                    }
                    break;
                case "/slap":
                    if (arrayLength == 1) {
                        final String nick = parsedArray.get(0);
                        server.getServerCallBus().sendSlap(channelName, nick);
                        return;
                    }
                    break;
                default:
                    onParseServerCommand(server, message);
                    return;
            }
        } else {
            server.getServerCallBus().sendMessageToChannel(channelName, message);
            return;
        }
        onUnknownEvent(server, message);
    }

    public static void onParseUserMessage(final Server server, final String userNick,
            final String message) {
        final ArrayList<String> parsedArray = IRCUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        if (command.startsWith("/")) {
            switch (command) {
                case "/me":
                    final String action = IRCUtils.convertArrayListToString(parsedArray);
                    server.getServerCallBus().sendActionToUser(userNick, action);
                    return;
                case "/close":
                case "/c":
                    if (arrayLength == 0) {
                        server.getServerCallBus().sendClosePrivateMessage(server
                                .getPrivateMessageUserIfExists(userNick));
                        return;
                    }
                    break;
                default:
                    onParseServerCommand(server, message);
                    return;
            }
        } else {
            server.getServerCallBus().sendMessageToUser(userNick, message);
            return;
        }
        onUnknownEvent(server, message);
    }

    public static void onParseServerMessage(final Server server, final String message) {
        if (message.startsWith("/")) {
            onParseServerCommand(server, message);
            return;
        }
        onUnknownEvent(server, message);
    }

    private static void onParseServerCommand(final Server server,
            final String rawLine) {
        final ArrayList<String> parsedArray = IRCUtils.splitRawLine(rawLine, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        switch (command) {
            case "/join":
            case "/j":
                if (arrayLength == 1) {
                    final String channelName = parsedArray.get(0);
                    server.getServerCallBus().sendJoin(channelName);
                    return;
                }
                break;
            case "/msg":
                if (arrayLength >= 1) {
                    final String nick = parsedArray.remove(0);
                    final String message = parsedArray.size() >= 1 ? IRCUtils
                            .convertArrayListToString(parsedArray) : "";
                    server.getServerCallBus().sendMessageToUser(nick, message);
                    return;
                }
                break;
            case "/nick":
                if (arrayLength == 1) {
                    final String newNick = parsedArray.get(0);
                    server.getServerCallBus().sendNickChange(newNick);
                    return;
                }
                break;
            case "/quit":
                if (arrayLength == 0) {
                    server.getServerCallBus().sendDisconnect();
                    return;
                }
                break;
            case "/whois":
                if (arrayLength == 1) {
                    server.getServerCallBus().sendUserWhois(parsedArray.get(0));
                    return;
                }
                break;
            case "/ns":
                if (arrayLength > 1) {
                    final String message = IRCUtils.convertArrayListToString(parsedArray);
                    server.getServerCallBus().sendMessageToUser("NickServ", message);
                    return;
                }
                break;
            case "/raw":
                server.getServerCallBus().sendRawLine(IRCUtils.convertArrayListToString
                        (parsedArray));
                return;
            default:
                if (command.startsWith("/")) {
                    server.getServerCallBus().sendRawLine(command.substring(1) + IRCUtils
                            .convertArrayListToString(parsedArray));
                    return;
                }
                break;
        }
        onUnknownEvent(server, rawLine);
    }

    private static void onUnknownEvent(final Server server, final String rawLine) {
        server.getServerCallBus().sendUnknownEvent(rawLine + " is not a valid command");
    }
}