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
import com.fusionx.androidirclibrary.ChannelUser;
import com.fusionx.androidirclibrary.PrivateMessageUser;
import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.UserChannelInterface;
import com.fusionx.androidirclibrary.communication.MessageSender;
import com.fusionx.androidirclibrary.constants.ServerCommands;
import com.fusionx.androidirclibrary.event.ChannelEvent;
import com.fusionx.androidirclibrary.event.Event;
import com.fusionx.androidirclibrary.event.QuitEvent;
import com.fusionx.androidirclibrary.util.IRCUtils;

import java.util.ArrayList;
import java.util.Set;

class ServerCommandParser {

    private final UserChannelInterface mUserChannelInterface;

    private final Server mServer;

    private final MessageSender mSender;

    ServerCommandParser(ServerLineParser parser) {
        mServer = parser.getServer();

        mUserChannelInterface = mServer.getUserChannelInterface();

        mSender = MessageSender.getSender(mServer.getTitle());
    }

    // The server is sending a command to us - parse what it is
    Event parseCommand(final ArrayList<String> parsedArray, final String rawLine) {
        final String rawSource = parsedArray.get(0);
        final String command = parsedArray.get(1).toUpperCase();

        if (command.equals(ServerCommands.Privmsg)) {
            final String message = parsedArray.get(3);
            if (message.startsWith("\u0001") && message.endsWith("\u0001")) {
                final String strippedMessage = message.substring(1, message.length() - 1);
                return parseCTCPCommand(parsedArray, strippedMessage, rawSource);
            } else {
                return parsePRIVMSGCommand(parsedArray, rawSource);
            }
        } else if (command.equals(ServerCommands.Join)) {
            return parseChannelJoin(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Notice)) {
            final String message = parsedArray.get(3);
            if (message.startsWith("\u0001") && message.endsWith("\u0001")) {
                final String strippedMessage = message.substring(1, message.length() - 1);
                return parseCTCPCommand(parsedArray, strippedMessage, rawSource);
            } else {
                return parseNotice(parsedArray, rawSource);
            }
        } else if (command.equals(ServerCommands.Part)) {
            return parseChannelPart(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Mode)) {
            return parseModeChange(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Quit)) {
            return parseServerQuit(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Nick)) {
            return parseNickChange(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Topic)) {
            return parseTopicChange(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Kick)) {
            return parseKick(parsedArray, rawSource);
        } else if (command.equals(ServerCommands.Invite)) {
            return parseInvite(parsedArray, rawSource);
        } else {// Not sure what to do here - TODO
            ///if (DEBUG) {
            //    Log.v(LOG_TAG, rawLine);
            //}
            return new Event(rawLine);
        }
    }

    private Event parseInvite(ArrayList<String> parsedArray, String rawSource) {
        final String invitingNick = IRCUtils.getNickFromRaw(rawSource);
        if (parsedArray.get(2).equals(mServer.getUser().getNick())) {
            final String channelName = parsedArray.get(3);
            mSender.sendInviteEvent(mServer, channelName);
        } else {
            // TODO - fix up what should happen here
        }
        return null;
    }

    private Event parseKick(ArrayList<String> parsedArray, String rawSource) {
        final String channelName = parsedArray.get(2);
        final String kickedNick = parsedArray.get(3);

        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final ChannelUser kickedUser = mUserChannelInterface.getUser(kickedNick);
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        if (kickedUser.equals(mServer.getUser())) {
            String message = ""; //String.format(mContext.getString(R.string
            //.parser_user_kicked_channel), channel.getName(),
            // user.getPrettyNick(channel));
            // If you have 4 strings in the array, the last must be the reason for parting
            //message += (parsedArray.size() == 5) ? " " + String.format(mContext.getString(R
            //        .string.parser_reason),
            //        parsedArray.get(4).replace("\"", "")) : "";
            final Event event = mSender.sendGenericServerEvent(mServer, message);

            mSender.sendKicked(channel.getName());
            mUserChannelInterface.removeChannel(channel);
            return event;
        } else {
            String message = "";//String.format(mContext.getString(R.string
            // .parser_kicked_channel),
            //kickedUser.getPrettyNick(channel), user.getPrettyNick(channel));
            // If you have 4 strings in the array, the last must be the reason for parting
            //message += (parsedArray.size() == 5) ? " " + String.format(mContext.getString(R
            //        .string.parser_reason),
            //        parsedArray.get(4).replace("\"", "")) : "";

            channel.onDecrementUserType(user.getChannelPrivileges(channel));

            final Event event = mSender.sendGenericChannelEvent(channel, message, true);
            mUserChannelInterface.decoupleUserAndChannel(kickedUser, channel);
            return event;
        }
    }

    private Event parseNotice(final ArrayList<String> parsedArray, final String rawSource) {
        final String sendingUser = IRCUtils.getNickFromRaw(rawSource);
        final String recipient = parsedArray.get(2);
        final String notice = parsedArray.get(3);

        final String formattedNotice = "";//String.format(mContext.getString(R.string
        //.parser_notice), sendingUser, notice);
        /*if (MiscUtils.isChannel(recipient.charAt(0))) {
            return mSender.sendGenericChannelEvent(mUserChannelInterface.getChannel(recipient),
                    formattedNotice, false);
        } else*/
        if (recipient.equals(mServer.getUser().getNick())) {
            final PrivateMessageUser user = mServer.getPrivateMessageUser(sendingUser);
            if (mServer.getUser().isPrivateMessageOpen(user)) {
                return mServer.onPrivateMessage(user, notice, false);
            } else {
                return mSender.sendSwitchToServerEvent(mServer, formattedNotice);
            }
            //} else if (DEBUG) {
            //    throw new IllegalArgumentException();
        } else {
            return new Event("unknown");
        }
    }

    private Event parseCTCPCommand(final ArrayList<String> parsedArray, final String message,
            final String rawSource) {
        if (message.startsWith("ACTION")) {
            return parseAction(parsedArray, rawSource);
        } else if (message.startsWith("VERSION")) {
            final String nick = IRCUtils.getNickFromRaw(rawSource);
            mServer.getWriter().sendVersion(nick, mServer.toString());
            return new Event(rawSource);
            // TODO - figure out what should be done here
            //} else if (DEBUG) {
            // TODO - add more things here
            //    throw new IllegalStateException();
        } else {
            return new Event("Non-DEBUG");
        }
    }

    private Event parsePRIVMSGCommand(final ArrayList<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        final String recipient = parsedArray.get(2);
        final String message = parsedArray.get(3);

        // TODO - optimize this
        //if (!MiscUtils.getIgnoreList(mContext, mServer.getTitle().toLowerCase()).contains(nick)
        // ) {
        //    if (MiscUtils.isChannel(recipient.charAt(0))) {
        final ChannelUser sendingUser = mUserChannelInterface.getUser(nick);
        final Channel channel = mUserChannelInterface.getChannel(recipient);
        return mSender.sendMessageToChannel(mServer.getUser().getNick(), channel,
                sendingUser.getBracketedNick(channel), message);
        //} else {
        //    final PrivateMessageUser sendingUser = mServer.getPrivateMessageUser(nick);
        //    return mServer.onPrivateMessage(sendingUser, message, false);
        //}
        //} else {
        //    return new Event(message);
        //}
    }

    private Event parseAction(ArrayList<String> parsedArray, String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        final String recipient = parsedArray.get(2);
        final String action = parsedArray.get(3).replace("ACTION ", "");

        //if (!MiscUtils.getIgnoreList(mContext, mServer.getTitle().toLowerCase()).contains(nick)
        // ) {
        //if (MiscUtils.isChannel(recipient.charAt(0))) {
        final ChannelUser sendingUser = mUserChannelInterface.getUser(nick);
        return mSender.sendChannelAction(mServer.getUser().getNick(),
                mUserChannelInterface.getChannel(recipient), sendingUser, action);
            /*} else {
                final PrivateMessageUser sendingUser = mServer.getPrivateMessageUser(nick);
                return mServer.onPrivateAction(sendingUser, action, false);
            }*/
        //} else {
        //    return new Event(action);
        //}
    }

    private ChannelEvent parseTopicChange(final ArrayList<String> parsedArray,
            final String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Channel channel = mUserChannelInterface.getChannel(parsedArray.get(2));
        final String setterNick = user.getPrettyNick(channel);
        final String newTopic = parsedArray.get(3);

        final String message = "";//String
        //.format(mContext.getString(R.string.parser_topic_changed), newTopic, setterNick);
        return mSender.sendGenericChannelEvent(channel, message, false);
    }

    private Event parseNickChange(ArrayList<String> parsedArray, String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Set<Channel> channels = user.getChannels();
        final String oldNick = user.getColorfulNick();
        user.setNick(parsedArray.get(2));

        final String message = "";/*user instanceof AppUser ?
                String.format(mContext.getString(R.string.parser_appuser_nick_changed),
                        oldNick, user.getColorfulNick()) :
                String.format(mContext.getString(R.string.parser_other_user_nick_change),
                        oldNick, user.getColorfulNick());*/

        if (channels != null) {
            for (final Channel channel : channels) {
                mSender.sendGenericChannelEvent(channel, message, true);
                channel.getUsers().update(user, channel);
            }
        }
        return new Event(user.getNick());
    }

    private Event parseModeChange(final ArrayList<String> parsedArray, final String rawSource) {
        final String sendingUser = IRCUtils.getNickFromRaw(rawSource);
        final String recipient = parsedArray.get(2);
        final String mode = parsedArray.get(3);
        //if (MiscUtils.isChannel(recipient.charAt(0))) {
        // The recipient is a channel (i.e. the mode of a user in the channel is being changed
        // or possibly the mode of the channel itself)
        final Channel channel = mUserChannelInterface.getChannel(recipient);
        if (parsedArray.size() == 4) {
            // User not specified - therefore channel mode is being changed
            // TODO - implement this?
            return new Event(mode);
        } else if (parsedArray.size() == 5) {
            // User specified - therefore user mode in channel is being changed
            final String userRecipient = parsedArray.get(4);
            final ChannelUser user = mUserChannelInterface.getUserFromRaw(userRecipient);
            if (user != null) {
                final String message = user.onModeChange(sendingUser, channel,
                        mode);
                return mSender.sendGenericChannelEvent(channel, message, true);
                //} else if (DEBUG) {
                //    throw new NullPointerException(rawSource);
            } else {
                return new Event("NON-DEBUG");
            }
        } else {
            //MiscUtils.removeFirstElementFromList(parsedArray, 4);
            final ChannelUser user = mUserChannelInterface.getUserIfExists(sendingUser);
            final String nick = (user == null) ? sendingUser : user.getPrettyNick(channel);
            final String message = "";/*String.format(mContext.getString(R.string
                        .parser_mode_changed), mode,
                        MiscUtils.convertArrayListToString(parsedArray), nick);*/
            return mSender.sendGenericChannelEvent(channel, message, true);
        }
        //} else {
        // A user is changing a mode about themselves
        // TODO - implement this?
        // return new Event(mode);
        //}
    }

    private Event parseChannelJoin(final ArrayList<String> parsedArray, final String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Channel channel = mUserChannelInterface.getChannel(parsedArray.get(2));
        mUserChannelInterface.coupleUserAndChannel(user, channel);

        if (user.equals(mServer.getUser())) {
            return mSender.sendChanelJoined(channel.getName());
        } else {
            return mSender.sendGenericChannelEvent(channel, "", true);
            //String.format(mContext.getString(R.string.parser_joined_channel),
            //        user.getPrettyNick(channel)), true);
        }
    }

    private Event parseChannelPart(final ArrayList<String> parsedArray, final String rawSource) {
        final String channelName = parsedArray.get(2);

        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        final Channel channel = mUserChannelInterface.getChannel(channelName);
        if (user.equals(mServer.getUser())) {
            mSender.sendChanelParted(channel.getName());
            mUserChannelInterface.removeChannel(channel);
            return new Event(channelName);
        } else {
            String message = "";//String.format(mContext.getString(R.string.parser_parted_channel),
            //user.getPrettyNick(channel));
            // If you have 4 strings in the array, the last must be the reason for parting
            //message += (parsedArray.size() == 4) ? " " + String.format(mContext.getString(R
            //        .string.parser_reason),
            // parsedArray.get(3).replace("\"", "")) : "";

            channel.onDecrementUserType(user.getChannelPrivileges(channel));

            final Event event = mSender.sendGenericChannelEvent(channel, message, true);
            mUserChannelInterface.decoupleUserAndChannel(user, channel);
            return event;
        }
    }

    private Event parseServerQuit(final ArrayList<String> parsedArray, final String rawSource) {
        final ChannelUser user = mUserChannelInterface.getUserFromRaw(rawSource);
        if (user.equals(mServer.getUser())) {
            // TODO - improve this
            return new QuitEvent();
        } else {
            final Set<Channel> list = mUserChannelInterface.removeUser(user);
            if (list != null) {
                for (final Channel channel : list) {
                    final String message = "";//String.format(mContext.getString(R.string
                    //.parser_quit_server),
                    //user.getPrettyNick(channel)) +
                    // If you have 3 strings in the array, the last must be the reason for
                    // quitting
                    //((parsedArray.size() == 3) ? " " +
                    //String.format(mContext.getString(R.string.parser_reason),
                    //                StringUtils.remove(parsedArray.get(2), "\"")) : "");
                    mSender.sendGenericChannelEvent(channel, message, true);
                    channel.onDecrementUserType(user.getChannelPrivileges(channel));
                }
            }
            return new Event(rawSource);
        }
    }
}
