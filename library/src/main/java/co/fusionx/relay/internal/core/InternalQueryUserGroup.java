package co.fusionx.relay.internal.core;

import com.google.common.base.Optional;

import java.util.Collection;

import co.fusionx.relay.core.QueryUserGroup;

public interface InternalQueryUserGroup extends QueryUserGroup {

    @Override
    public Collection<InternalQueryUser> getQueryUsers();

    @Override
    public Optional<InternalQueryUser> getQueryUser(final String nick);

    public void removeQueryUser(final InternalQueryUser user);
}