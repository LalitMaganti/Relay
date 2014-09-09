package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import java.util.Collection;
import java.util.LinkedHashSet;

import javax.inject.Inject;

import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.sender.RelayQuerySender;
import co.fusionx.relay.internal.sender.PacketSender;

public class RelayQueryUserGroup implements InternalQueryUserGroup {

    private final GenericBus<Event> mSessionBus;

    private final PacketSender mSender;

    private final LibraryUser mUser;

    private final ConnectionConfiguration mConfiguration;

    private final Collection<InternalQueryUser> mQueryUsers;

    @Inject
    public RelayQueryUserGroup(final GenericBus<Event> sessionBus,
            final ConnectionConfiguration configuration, final PacketSender sender,
            final InternalUserChannelGroup group) {
        mSessionBus = sessionBus;
        mConfiguration = configuration;
        mSender = sender;
        mUser = group.getUser();

        mQueryUsers = new LinkedHashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<InternalQueryUser> getQueryUsers() {
        return mQueryUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<InternalQueryUser> getQueryUser(final String nick) {
        return FluentIterable.from(mQueryUsers)
                .filter(u -> nick.equals(u.getNick().getNickAsString()))
                .first();
    }

    @Override
    public InternalQueryUser addQueryUser(final String nick) {
        final RelayQuerySender sender = new RelayQuerySender(mSender, mUser, this);
        final InternalQueryUser user = new RelayQueryUser(mSessionBus, mConfiguration, sender,
                nick);

        // Horrible but has to be done - see the comment on the method
        sender.setQueryUser(user);
        mQueryUsers.add(user);

        return user;
    }

    @Override
    public void removeQueryUser(final InternalQueryUser user) {
        mQueryUsers.remove(user);
        user.markInvalid();
    }
}