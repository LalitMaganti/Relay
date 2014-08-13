package com.fusionx.relay;

import com.google.common.base.Optional;

import java.util.Collection;

public interface UserChannelInterface {

    /**
     * Get the channel by name from the list of channels which have been joined by the user
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

    /**
     * Get a collection of the users we are querying on this server
     */
    public Collection<? extends QueryUser> getQueryUsers();

    /**
     * Get the user by nick from list of users we are querying
     *
     * @param nick the nick of user to retrieve
     * @return an optional possibly containing the query user matching the specified nick
     */
    public Optional<? extends QueryUser> getQueryUser(final String nick);
}