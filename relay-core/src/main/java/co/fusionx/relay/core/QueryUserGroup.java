package co.fusionx.relay.core;

import com.google.common.base.Optional;

import java.util.Collection;

import co.fusionx.relay.conversation.QueryUser;
import co.fusionx.relay.internal.core.InternalQueryUser;

public interface QueryUserGroup {

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

    /**
     * Start a query with the specified user or gets an existing query if it exists
     *
     * @param nick the nick to start the query
     * @return the query user created
     */
    public InternalQueryUser getOrAddQueryUser(final String nick);
}