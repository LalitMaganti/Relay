package com.fusionx.relay.parser;

import com.fusionx.relay.AppUser;
import com.fusionx.relay.Channel;
import com.fusionx.relay.ChannelUser;
import com.fusionx.relay.PrivateMessageUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.UserChannelInterface;
import com.fusionx.relay.communication.ServerEventBus;
import com.fusionx.relay.constants.ServerCommands;
import com.fusionx.relay.event.ChannelEvent;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.event.QuitEvent;
import com.fusionx.relay.event.VersionEvent;
import com.fusionx.relay.interfaces.EventResponses;
import com.fusionx.relay.misc.InterfaceHolders;
import com.fusionx.relay.util.IRCUtils;

import java.util.ArrayList;
import java.util.Set;

class ServerCommandParser {

    private final UserChannelInterface mUserChannelInterface;

    private final Server mServer;

    private final ServerEventBus mServerEventBus;

    private final EventResponses mEventResponses;

    ServerCommandParser(final ServerLineParser parser) {
        mServer = parser.getServer();
        mUserChannelInterface = mServer.getUserChannelInterface();
        mServerEventBus = mServer.getServerEventBus();
        mEventResponses = InterfaceHolders.getEventResponses();
    }

    // The server is sending a command to us - parse what it is
    Event onParseServerCommand(final ArrayList<String> parsedArray, final String rawLine) {
        final String rawSource = parsedArray.get(0);
        final String command = parsedArray.get(1).toUpperCase();

        if (command.equals(ServerCommands.Privmsg)) {
            final String message = parsedArray.get(3);
            if (message.startsWith("\u0001") && message.endsWith("\u0001")) {
                final String strippedMessage = message.substring(1, message.length() - 1);
                return onCTCP(parsedArray, strippedMessage, rawSource);
            } else {
                return onPRIVMSG(parsedArray, rawSource);
            }
        } else if (command.equals(ServerCommands.Join)) {
            return onChannelJoin(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Notice)) {
            final String message = parsedArray.get(3);
            if (message.startsWith("\u0001") && message.endsWith("\u0001")) {
                final String strippedMessage = message.substring(1, message.length() - 1);
                return onCTCP(parsedArray, strippedMessage, rawSource);
            } else {
                return onNotice(parsedArray, rawSource);
            }
        } else if (command.equals(ServerCommands.Part)) {
            return onChannelPart(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Mode)) {
            return onModeChanged(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Quit)) {
            return onQuit(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Nick)) {
            return onNickChange(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Topic)) {
            return onChannelTopicChanged(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Kick)) {
            return onChannelKick(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Invite)) {
            return onChannelInvite(parsedArray, rawSource);
        } else {
            // Not sure what to do here - TODO
            return new Event(rawLine);
        }
    }

    private Event onNickChange(ArrayList<String> parsedArray, String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Set<Channel> channels = user.getChannels();
        final String oldNick = user.getColorfulNick();
        user.setNick(parsedArray.get(2));

        final String message = mEventResponses.getNickChangedMessage(oldNick,
                user.getColorfulNick(), user instanceof AppUser);

        if (channels != null) {
            for (final Channel channel : channels) {
                mServerEventBus.sendGenericChannelEvent(channel, message, true);
                channel.getUsers().update(user, channel);
            }
        }
        return new Event(user.getNick());
    }

    private Event onModeChanged(final ArrayList<String> parsedArray, final String rawSource) {
        final String sendingUser = IRCUtils.getNickFromRaw(rawSource);
        final String recipient = parsedArray.get(2);
        final String mode = parsedArray.get(3);
        if (Channel.isChannelPrefix(recipient.charAt(0))) {
            // The recipient is a channel (i.e. the mode of a user in the channel is being changed
            // or possibly the mode of the channel itself)
            final Channel channel = mUserChannelInterface.getChannel(recipient);
            final int messageLength = parsedArray.size();
            if (messageLength == 4) {
                // User not specified - therefore channel mode is being changed
                // TODO - implement this?
                return new Event(mode);
            } else if (messageLength == 5) {
                // User specified - therefore user mode in channel is being changed
                final String nick = IRCUtils.getNickFromRaw(parsedArray.get(4));
                final ChannelUser user = mUserChannelInterface.getUserIfExists(nick);
                if (user != null) {
                    final String message = user.onModeChange(sendingUser, channel, mode);
                    return mServerEventBus.sendGenericChannelEvent(channel, message, true);
                } else {
                    return new Event("");
                }
            } else {
                IRCUtils.removeFirstElementFromList(parsedArray, 4);
                final ChannelUser user = mUserChannelInterface.getUserIfExists(sendingUser);
                final String nick = (user == null) ? sendingUser : user.getPrettyNick(channel);
                final String message = mEventResponses.getModeChangedMessage(mode,
                        IRCUtils.convertArrayListToString(parsedArray), nick);
                return mServerEventBus.sendGenericChannelEvent(channel, message, true);
            }
        } else {
            // A user is changing a mode about themselves
            // TODO - implement this?
            return new Event(mode);
        }
    }

    private Event onNotice(final ArrayList<String> parsedArray, final String rawSource) {
        final String sendingUser = IRCUtils.getNickFromRaw(rawSource);
        final String recipient = parsedArray.get(2);
        final String notice = parsedArray.get(3);

        final String formattedNotice = mEventResponses.getNoticeMessage(sendingUser, notice);
        if (Channel.isChannelPrefix(recipient.charAt(0))) {
            final Channel channel = mUserChannelInterface.getChannel(recipient);
            return mServerEventBus.sendGenericChannelEvent(channel, formattedNotice, false);
        } else if (recipient.equals(mServer.getUser().getNick())) {
            final PrivateMessageUser user = mServer.getPrivateMessageUser(sendingUser);
            if (mServer.getUser().isPrivateMessageOpen(user)) {
                return mServer.onPrivateMessage(user, notice, false);
            } else {
                return mServerEventBus.sendSwitchToServerEvent(mServer, formattedNotice);
            }
        } else {
            return new Event("unknown");
        }
    }

    // CTCP starts here
    private Event onCTCP(final ArrayList<String> parsedArray, final String message,
            final String rawSource) {
        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            return onParseAction(parsedArray, rawSource);
        } else if (message.startsWith("VERSION")) {
            final String nick = IRCUtils.getNickFromRaw(rawSource);
            // TODO - get the version from the app
            mServer.getServerCallBus().post(new VersionEvent(nick, mServer.toString()));
            return new Event(rawSource);
        } else {
            return new Event("");
        }
    }

    private Event onParseAction(final ArrayList<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        final String action = parsedArray.get(3).replace("ACTION ", "");

        if (!InterfaceHolders.getPreferences().shouldIgnoreUser(nick)) {
            final String recipient = parsedArray.get(2);
            if (Channel.isChannelPrefix(recipient.charAt(0))) {
                return onParseChannelAction(recipient, nick, action);
            } else {
                final PrivateMessageUser sendingUser = mServer.getPrivateMessageUser(nick);
                return mServer.onPrivateAction(sendingUser, action, false);
            }
        } else {
            return new Event(action);
        }
    }
    // CTCP ends here

    private Event onPRIVMSG(final ArrayList<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        final String message = parsedArray.get(3);

        // TODO - optimize this
        if (!InterfaceHolders.getPreferences().shouldIgnoreUser(nick)) {
            final String recipient = parsedArray.get(2);
            if (Channel.isChannelPrefix(recipient.charAt(0))) {
                return parseChannelMessage(nick, recipient, message);
            } else {
                final PrivateMessageUser sendingUser = mServer.getPrivateMessageUser(nick);
                return mServer.onPrivateMessage(sendingUser, message, false);
            }
        } else {
            return new Event(message);
        }
    }

    private Event onQuit(final ArrayList<String> parsedArray, final String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        if (user.equals(mServer.getUser())) {
            // TODO - improve this
            return new QuitEvent("");
        } else {
            final Set<Channel> list = mUserChannelInterface.removeUser(user);
            if (list != null) {
                for (final Channel channel : list) {
                    final String reason = parsedArray.size() == 4 ?
                            parsedArray.get(3).replace("\"", "") : "";
                    final String nick = user.getPrettyNick(channel);

                    final String message = mEventResponses.getQuitMessage(nick, reason);
                    mServerEventBus.sendGenericChannelEvent(channel, message, true);
                    channel.onDecrementUserType(user.getChannelPrivileges(channel));
                }
            }
            return new Event(rawSource);
        }
    }

    // Channel parsing starts here
    private ChannelEvent parseChannelMessage(final String sendingNick,
            final String channelName, final String message) {
        final ChannelUser sendingUser = mUserChannelInterface.getUserIfExists(sendingNick);
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        // This occurs rarely - usually on ZNCs - for example the ZNC buffer starts with a
        // PRIVMSG from the nick ***. Also if someone said something on the channel during
        // the buffer but is not in the channel now then this will also happen
        if (sendingUser == null) {
            return mServerEventBus.onChannelMessage(mServer.getUser(), channel,
                    sendingNick, message);
        } else {
            return mServerEventBus.onChannelMessage(mServer.getUser(), channel,
                    sendingUser, message);
        }
    }

    private Event onParseChannelAction(final String channelName, final String userNick,
            final String action) {
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        final ChannelUser sendingUser = mUserChannelInterface.getUserIfExists(userNick);
        // This occurs rarely - usually on ZNCs. Also if someone performed an action on the
        // channel during the buffer but is not in the channel now then this will also happen
        if (sendingUser == null) {
            return mServerEventBus.onChannelAction(mServer.getUser(), channel,
                    userNick, action);
        } else {
            return mServerEventBus.onChannelAction(mServer.getUser(), channel,
                    sendingUser, action);
        }
    }

    private ChannelEvent onChannelTopicChanged(final ArrayList<String> parsedArray,
            final String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Channel channel = mUserChannelInterface.getChannel(parsedArray.get(2));
        final String setterNick = user.getPrettyNick(channel);
        final String newTopic = parsedArray.get(3);

        final String message = mEventResponses.getTopicChangedMessage(setterNick,
                channel.getTopic(), newTopic);
        channel.setTopic(newTopic);
        return mServerEventBus.sendGenericChannelEvent(channel, message, false);
    }

    private Event onChannelJoin(final ArrayList<String> parsedArray, final String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Channel channel = mUserChannelInterface.getChannel(parsedArray.get(2));
        mUserChannelInterface.coupleUserAndChannel(user, channel);

        if (user.equals(mServer.getUser())) {
            return mServerEventBus.onChannelJoined(channel.getName());
        } else {
            final String message = mEventResponses.getJoinMessage(user.getPrettyNick(channel));
            return mServerEventBus.sendGenericChannelEvent(channel, message, true);
        }
    }

    private Event onChannelPart(final ArrayList<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(2);

        final String userNick = IRCUtils.getNickFromRaw(rawSource);
        final ChannelUser user = mUserChannelInterface.getUserIfExists(userNick);
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        if (user.equals(mServer.getUser())) {
            mServerEventBus.onChannelParted(channel.getName());
            mUserChannelInterface.removeChannel(channel);
            return new Event(channelName);
        } else {
            final String reason = parsedArray.size() == 4 ?
                    parsedArray.get(3).replace("\"", "") : "";
            final String message = mEventResponses.getPartMessage(user.getPrettyNick(channel),
                    reason);

            // Decrement the type before sending a message/decoupling so it's picked up
            channel.onDecrementUserType(user.getChannelPrivileges(channel));

            final Event event = mServerEventBus.sendGenericChannelEvent(channel, message, true);
            mUserChannelInterface.decoupleUserAndChannel(user, channel);
            return event;
        }
    }

    private Event onChannelKick(ArrayList<String> parsedArray, String rawSource) {
        final String channelName = parsedArray.get(2);
        final String kickedNick = parsedArray.get(3);

        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final ChannelUser kickedUser = mUserChannelInterface.getUser(kickedNick);
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"",
                "") : "";
        final String kickingUserNick = user.getPrettyNick(channel);
        if (kickedUser.equals(mServer.getUser())) {
            final String message = mEventResponses.getOnUserKickedMessage(channel.getName(),
                    kickingUserNick, reason);
            final Event event = mServerEventBus.sendGenericServerEvent(mServer, message);

            mServerEventBus.onKicked(channel.getName());
            mUserChannelInterface.removeChannel(channel);
            return event;
        } else {
            final String message = mEventResponses.getUserKickedMessage(kickedUser.getPrettyNick
                    (channel), kickingUserNick, reason);

            channel.onDecrementUserType(user.getChannelPrivileges(channel));

            final Event event = mServerEventBus.sendGenericChannelEvent(channel, message, true);
            mUserChannelInterface.decoupleUserAndChannel(kickedUser, channel);
            return event;
        }
    }

    private Event onChannelInvite(ArrayList<String> parsedArray, String rawSource) {
        final String invitingNick = IRCUtils.getNickFromRaw(rawSource);
        if (parsedArray.get(2).equals(mServer.getUser().getNick())) {
            final String channelName = parsedArray.get(3);
            mServerEventBus.sendInviteEvent(mServer, channelName);
        } else {
            // TODO - fix up what should happen here
        }
        return null;
    }
    // Channel parsing ends here
}
