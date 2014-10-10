package co.fusionx.relay.parser;

import com.google.common.base.Optional;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.conversation.QueryUser;
import co.fusionx.relay.provider.SettingsProvider;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.util.ParseUtils;
import co.fusionx.relay.util.StringUtils;

@Singleton
public class UserInputParser {

    private final InternalServer mServer;

    private final SettingsProvider mSettingsProvider;

    private final InternalQueryUserGroup mQueryUserGroup;

    @Inject
    public UserInputParser(final InternalServer server, final SettingsProvider settingsProvider,
            final InternalQueryUserGroup queryUserGroup) {
        mServer = server;
        mSettingsProvider = settingsProvider;
        mQueryUserGroup = queryUserGroup;
    }

    private boolean parseUserCloseCommand(final int arrayLength, final QueryUser queryUser) {
        if (arrayLength == 0) {
            queryUser.close();
            return true;
        }
        return false;
    }

    public void parseServerMessage(final String message) {
        if (message.startsWith("/")) {
            onParseServerCommand(message);
            return;
        }
        onUnknownEvent(message);
    }

    private void onParseServerCommand(final String rawLine) {
        final List<String> parsedArray = ParseUtils.splitRawLine(rawLine, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        switch (command) {
            case "/join":
            case "/j":
                if (arrayLength == 1) {
                    final String channelName = parsedArray.get(0);
                    mServer.sendJoin(channelName);
                    return;
                }
                break;
            case "/msg":
                if (arrayLength >= 1) {
                    parseMsg(parsedArray);
                    return;
                }
                break;
            case "/nick":
                if (arrayLength == 1) {
                    final String newNick = parsedArray.get(0);
                    mServer.sendNick(newNick);
                    return;
                }
                break;
            case "/whois":
                if (arrayLength == 1) {
                    final String nick = parsedArray.get(0);
                    mServer.sendWhois(nick);
                    return;
                }
                break;
            case "/ns":
                if (arrayLength > 1) {
                    parseNsInput(parsedArray);
                    return;
                }
                break;
            case "/raw":
            case "/quote":
                mServer.sendRawLine(StringUtils.concatenateStringList(parsedArray));
                return;
            default:
                if (command.startsWith("/")) {
                    mServer.sendRawLine(command.substring(1) + " "
                            + StringUtils.concatenateStringList(parsedArray));
                    return;
                }
                break;
        }
        onUnknownEvent(rawLine);
    }

    private void parseNsInput(final List<String> parsedArray) {
        final QueryUser queryUser = mQueryUserGroup.getOrAddQueryUser("NickServ");
        final String message = StringUtils.concatenateStringList(parsedArray);
        queryUser.sendMessage(message);
    }

    private void parseMsg(final List<String> parsedArray) {
        final String nick = parsedArray.remove(0);
        final QueryUser queryUser = mQueryUserGroup.getOrAddQueryUser(nick);
        if (parsedArray.size() >= 1) {
            final String message = StringUtils.concatenateStringList(parsedArray);
            queryUser.sendMessage(message);
        }
    }

    private void onUnknownEvent(final String rawLine) {
        // server.getServerCallBus().sendUnknownEvent(rawLine + " is not a valid command");
    }

    /*
    public void parseDCCChatEvent(final RelayDCCChatConversation chatConnection,
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
                onParseServerCommand(message);
                return;
        }
        onUnknownEvent(message);
    }
    */

    public void parseChannelMessage(final Channel channel, final String message) {
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
                    final String action = StringUtils.concatenateStringList(parsedArray);
                    channel.sendAction(action);
                    return;
                }
                break;
            case "/part":
            case "/p":
                if (arrayLength == 0) {
                    channel.sendPart(Optional.fromNullable(mSettingsProvider.getPartReason()));
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
                            ? Optional.of(StringUtils.concatenateStringList(parsedArray))
                            : Optional.absent();
                    channel.sendKick(nick, reason);
                    return;
                }
                break;
            case "/topic":
                if (arrayLength >= 1) {
                    final String topic = StringUtils.concatenateStringList(parsedArray);
                    channel.sendTopic(topic);
                    return;
                }
                break;
            default:
                onParseServerCommand(message);
                return;
        }

        onUnknownEvent(message);
    }

    public void parseQueryMessage(final QueryUser queryUser, final String message) {
        final List<String> parsedArray = ParseUtils.splitRawLine(message, false);
        final String command = parsedArray.remove(0);
        final int arrayLength = parsedArray.size();

        if (!command.startsWith("/")) {
            queryUser.sendMessage(message);
            return;
        }
        switch (command) {
            case "/me":
                final String action = StringUtils.concatenateStringList(parsedArray);
                queryUser.sendAction(action);
                return;
            case "/close":
            case "/c":
                if (parseUserCloseCommand(arrayLength, queryUser)) {
                    return;
                }
                break;
            default:
                onParseServerCommand(message);
                return;
        }
        onUnknownEvent(message);
    }
}