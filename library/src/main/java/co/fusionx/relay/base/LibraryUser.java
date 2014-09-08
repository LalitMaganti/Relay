package co.fusionx.relay.base;

import com.google.common.base.Optional;

import java.util.Collection;

public interface LibraryUser extends ChannelUser {

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