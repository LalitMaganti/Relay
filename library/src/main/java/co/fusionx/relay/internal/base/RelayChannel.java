package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.HashSet;

import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.sender.ChannelSender;

public class RelayChannel extends RelayAbstractConversation<ChannelEvent>
        implements InternalChannel {

    // As set out in RFC2812
    private final static ImmutableList<Character> CHANNEL_PREFIXES = ImmutableList.of('#', '&',
            '+', '!');

    private final ChannelSender mChannelSender;

    private final String mChannelName;

    private final Collection<InternalChannelUser> mUsers;

    private final ConnectionConfiguration mConfiguration;

    RelayChannel(final GenericBus<Event> sessionBus, final ConnectionConfiguration configuration,
            final ChannelSender channelSender, final String channelName) {
        super(sessionBus);

        mConfiguration = configuration;
        mChannelSender = channelSender;
        mChannelName = channelName;

        mUsers = new HashSet<>();
    }

    /**
     * Returns whether a string is a channel name based on the first character of the string
     *
     * @param firstCharacter the first character of the string that is to be tested
     * @return whether the character can be one at the start of a channel
     */
    public static boolean isChannelPrefix(char firstCharacter) {
        return CHANNEL_PREFIXES.contains(firstCharacter);
    }

    // Helpers
    @Override
    public void clearInternalData() {
        // Clear the list of users
        mUsers.clear();
    }

    @Override
    public void addUser(final InternalChannelUser user) {
        mUsers.add(user);
    }

    @Override
    public void removeUser(final InternalChannelUser user) {
        mUsers.remove(user);
    }

    // Channel and Conversation interfaces

    /**
     * Returns a list of all the users currently in the channel
     *
     * @return list of users currently in the channel
     */
    @Override
    public Collection<InternalChannelUser> getUsers() {
        return mUsers;
    }

    /**
     * Gets the name of the channel
     *
     * @return the name of the channel
     */
    @Override
    public String getName() {
        return mChannelName;
    }

    /**
     * Returns the id of the channel which is simply its name
     *
     * @return the id (name) of the channel
     */
    @Override
    public String getId() {
        return mChannelName;
    }

    /*
     * A channel is equal to another if the servers are equal and if the channel's names are
     * equal regardless of case
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof InternalChannel)) {
            return false;
        }
        final RelayChannel otherChannel = (RelayChannel) o;
        return mConfiguration.getTitle()
                .equals(otherChannel.mConfiguration.getTitle())
                && otherChannel.getName().equalsIgnoreCase(mChannelName);
    }

    /*
     * The hashcode of a channel can simply be the same as that of its name
     */
    @Override
    public int hashCode() {
        return mChannelName.toLowerCase().hashCode();
    }

    /*
     * A channel's string representation is simply its name
     */
    @Override
    public String toString() {
        return mChannelName;
    }

    // ChannelSender interface
    @Override
    public void sendAction(final String action) {
        mChannelSender.sendAction(action);
    }

    @Override
    public void sendKick(final String userNick, final Optional<String> reason) {
        mChannelSender.sendKick(userNick, reason);
    }

    @Override
    public void sendMessage(final String message) {
        mChannelSender.sendMessage(message);
    }

    @Override
    public void sendPart(final Optional<String> reason) {
        mChannelSender.sendPart(reason);
    }

    @Override
    public void sendTopic(final String newTopic) {
        mChannelSender.sendTopic(newTopic);
    }

    @Override
    public void sendUserMode(final String userNick, final String mode) {
        mChannelSender.sendUserMode(userNick, mode);
    }
}