package com.fusionx.relay;

import java.util.Collection;

public interface UserChannelInterface {

    /**
     * Get the channel by name from the list of channels which have been joined by the user
     *
     * @param name the name of channel to retrieve
     * @return the channel matching the specified name or null if none match
     */
    public Channel getChannel(final String name);

    /**
     * Get the user by nick from the global list of users
     *
     * @param nick the nick of user to retrieve
     * @return the user matching the specified nick or null if none match
     */
    public ChannelUser getUser(final String nick);

    public Collection<? extends QueryUser> getQueryUsers();

    public QueryUser getQueryUser(final String nick);
}
