package com.fusionx.relay.parser;

import com.fusionx.relay.QueryUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.util.IRCUtils;

import java.util.List;

import java8.util.Optional;

public class UserInputParser {

    public static void onParseChannelMessage(final Server server, final String channelName,
            final String message) {
        final List<String> parsedArray = IRCUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        if (command.startsWith("/")) {
            switch (command) {
                case "/me":
                    if (arrayLength >= 1) {
                        final String action = IRCUtils.concatenateStringList(parsedArray);
                        server.getServerCallBus().sendActionToChannel(channelName, action);
                        return;
                    }
                    break;
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
                        final String reason = arrayLength >= 1 ? IRCUtils
                                .concatenateStringList(parsedArray) : "";
                        server.getServerCallBus().sendKick(channelName, nick, reason);
                        return;
                    }
                    break;
                case "/topic":
                    if (arrayLength >= 1) {
                        final String topic = IRCUtils.concatenateStringList(parsedArray);
                        server.getServerCallBus().sendTopic(channelName, topic);
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
        final List<String> parsedArray = IRCUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        if (command.startsWith("/")) {
            switch (command) {
                case "/me":
                    final String action = IRCUtils.concatenateStringList(parsedArray);
                    server.getServerCallBus().sendActionToQueryUser(userNick, action);
                    return;
                case "/close":
                case "/c":
                    if (parseUserCloseCommand(server, arrayLength, userNick)) {
                        return;
                    }
                    break;
                default:
                    onParseServerCommand(server, message);
                    return;
            }
        } else {
            server.getServerCallBus().sendMessageToQueryUser(userNick, message);
            return;
        }
        onUnknownEvent(server, message);
    }

    private static boolean parseUserCloseCommand(final Server server, final int arrayLength,
            final String userNick) {
        if (arrayLength != 0) {
            return false;
        }
        final Optional<? extends QueryUser> optional = server.getUserChannelInterface()
                .getQueryUser(userNick);
        if (optional.isPresent()) {
            server.getServerCallBus().sendCloseQuery(optional.get());
        } else {
            // This is probably a bug we need to fix
        }
        return true;
    }

    public static void onParseServerMessage(final Server server, final String message) {
        if (message.startsWith("/")) {
            onParseServerCommand(server, message);
            return;
        }
        onUnknownEvent(server, message);
    }

    private static void onParseServerCommand(final Server server, final String rawLine) {
        final List<String> parsedArray = IRCUtils.splitRawLine(rawLine, false);
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
                    final String message = parsedArray.size() >= 1 ? IRCUtils.concatenateStringList
                            (parsedArray) : "";
                    server.getServerCallBus().sendMessageToQueryUser(nick, message);
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
            case "/whois":
                if (arrayLength == 1) {
                    server.getServerCallBus().sendUserWhois(parsedArray.get(0));
                    return;
                }
                break;
            case "/ns":
                if (arrayLength > 1) {
                    final String message = IRCUtils.concatenateStringList(parsedArray);
                    server.getServerCallBus().sendMessageToQueryUser("NickServ", message);
                    return;
                }
                break;
            case "/raw":
            case "/quote":
                server.getServerCallBus().sendRawLine(IRCUtils.concatenateStringList
                        (parsedArray));
                return;
            default:
                if (command.startsWith("/")) {
                    server.getServerCallBus().sendRawLine(command.substring(1) + " " + IRCUtils
                            .concatenateStringList(parsedArray));
                    return;
                }
                break;
        }
        onUnknownEvent(server, rawLine);
    }

    private static void onUnknownEvent(final Server server, final String rawLine) {
        // server.getServerCallBus().sendUnknownEvent(rawLine + " is not a valid command");
    }
}