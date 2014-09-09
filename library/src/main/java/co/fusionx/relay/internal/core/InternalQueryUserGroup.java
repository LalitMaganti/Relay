package co.fusionx.relay.internal.core;

import com.google.common.base.Optional;

import java.util.Collection;

import co.fusionx.relay.core.QueryUserGroup;

public interface InternalQueryUserGroup extends QueryUserGroup {

    public Collection<InternalQueryUser> getQueryUsers();

    public Optional<InternalQueryUser> getQueryUser(final String nick);

    public InternalQueryUser addQueryUser(String nick);

    public void removeQueryUser(InternalQueryUser user);
}