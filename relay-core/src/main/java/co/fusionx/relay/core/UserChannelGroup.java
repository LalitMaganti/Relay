package co.fusionx.relay.core;

import com.google.common.base.Optional;

import co.fusionx.relay.conversation.Channel;

public interface UserChannelGroup {

    /**
     * Gets the user who is currently using the library
     *
     * @return the user of the library
     */
    public LibraryUser getUser();

    /**
     * Get the channel by name from the list of channels which have been joined by the library user
     *
     * @param name the name of channel to retrieve
     * @return an optional possibly containing the channel matching the specified name
     */
    public Optional<? extends Channel> getChannel(final String name);

    /**
     * Get the user by nick from the global list of users
     *
     * @param nick the nick of user to retrieve
     * @return an optional possibly containing the user matching the specified nick
     */
    public Optional<? extends ChannelUser> getUser(final String nick);
}