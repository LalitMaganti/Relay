package co.fusionx.relay.parser;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.QueryUser;
import co.fusionx.relay.base.Server;
import co.fusionx.relay.dcc.chat.DCCChatConversation;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.ParseUtils;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class UserInputParser {

    public static void onParseChannelMessage(final Channel channel, final String message) {
        final List<String> parsedArray = ParseUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        if (!command.startsWith("/")) {
            channel.sendMessage(message);
            return;
        }
        switch (command) {
            case "/me":
                if (arrayLength >= 1) {
                    final String action = IRCUtils.concatenateStringList(parsedArray);
                    channel.sendAction(action);
                    return;
                }
                break;
            case "/part":
            case "/p":
                if (arrayLength == 0) {
                    channel.sendPart(Optional.fromNullable(getPreferences().getPartReason()));
                    return;
                }
                break;
            case "/mode":
                if (arrayLength == 2) {
                    channel.sendUserMode(parsedArray.get(0), parsedArray.get(1));
                    return;
                }
                break;
            case "/kick":
                if (arrayLength >= 1) {
                    final String nick = parsedArray.remove(0);
                    final Optional<String> reason = arrayLength >= 1
                            ? Optional.of(IRCUtils.concatenateStringList(parsedArray))
                            : Optional.absent();
                    channel.sendKick(nick, reason);
                    return;
                }
                break;
            case "/topic":
                if (arrayLength >= 1) {
                    final String topic = IRCUtils.concatenateStringList(parsedArray);
                    channel.sendTopic(topic);
                    return;
                }
                break;
            default:
                onParseServerCommand(channel.getServer(), message);
                return;
        }

        onUnknownEvent(channel.getServer(), message);
    }

    public static void onParseUserMessage(final QueryUser queryUser, final String message) {
        final List<String> parsedArray = ParseUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        if (!command.startsWith("/")) {
            queryUser.sendMessage(message);
            return;
        }
        switch (command) {
            case "/me":
                final String action = IRCUtils.concatenateStringList(parsedArray);
                queryUser.sendAction(action);
                return;
            case "/close":
            case "/c":
                if (parseUserCloseCommand(arrayLength, queryUser)) {
                    return;
                }
                break;
            default:
                onParseServerCommand(queryUser.getServer(), message);
                return;
        }
        onUnknownEvent(queryUser.getServer(), message);
    }

    private static boolean parseUserCloseCommand(final int arrayLength, final QueryUser queryUser) {
        if (arrayLength == 0) {
            queryUser.close();
            return true;
        }
        return false;
    }

    public static void onParseServerMessage(final Server server, final String message) {
        if (message.startsWith("/")) {
            onParseServerCommand(server, message);
            return;
        }
        onUnknownEvent(server, message);
    }

    private static void onParseServerCommand(final Server server, final String rawLine) {
        final List<String> parsedArray = ParseUtils.splitRawLine(rawLine, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        switch (command) {
            case "/join":
            case "/j":
                if (arrayLength == 1) {
                    final String channelName = parsedArray.get(0);
                    server.sendJoin(channelName);
                    return;
                }
                break;
            case "/msg":
                if (arrayLength >= 1) {
                    final String nick = parsedArray.remove(0);
                    final String message = parsedArray.size() >= 1 ? IRCUtils.concatenateStringList
                            (parsedArray) : "";
                    server.sendQuery(nick, message);
                    return;
                }
                break;
            case "/nick":
                if (arrayLength == 1) {
                    final String newNick = parsedArray.get(0);
                    server.sendNick(newNick);
                    return;
                }
                break;
            case "/whois":
                if (arrayLength == 1) {
                    final String nick = parsedArray.get(0);
                    server.sendWhois(nick);
                    return;
                }
                break;
            case "/ns":
                if (arrayLength > 1) {
                    final String message = IRCUtils.concatenateStringList(parsedArray);
                    server.sendQuery("NickServ", message);
                    return;
                }
                break;
            case "/raw":
            case "/quote":
                server.sendRawLine(IRCUtils.concatenateStringList(parsedArray));
                return;
            default:
                if (command.startsWith("/")) {
                    server.sendRawLine(command.substring(1) + " "
                            + IRCUtils.concatenateStringList(parsedArray));
                    return;
                }
                break;
        }
        onUnknownEvent(server, rawLine);
    }

    private static void onUnknownEvent(final Server server, final String rawLine) {
        // server.getServerCallBus().sendUnknownEvent(rawLine + " is not a valid command");
    }

    public static void onParseDCCChatEvent(final DCCChatConversation chatConnection,
            final String message) {
        final List<String> parsedArray = ParseUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        if (!command.startsWith("/")) {
            chatConnection.sendMessage(message);
            return;
        }
        switch (command) {
            case "/me":
                final String action = IRCUtils.concatenateStringList(parsedArray);
                chatConnection.sendAction(action);
                return;
            case "/close":
            case "/c":
                if (arrayLength == 0) {
                    chatConnection.closeChat();
                    return;
                }
                break;
            default:
                onParseServerCommand(chatConnection.getServer(), message);
                return;
        }
        onUnknownEvent(chatConnection.getServer(), message);
    }
}