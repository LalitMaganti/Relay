package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import java.util.Collection;
import java.util.LinkedHashSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import co.fusionx.relay.base.ConnectionConfiguration;
import co.fusionx.relay.base.LibraryUser;
import co.fusionx.relay.base.QueryUserGroup;
import co.fusionx.relay.base.UserChannelGroup;
import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.sender.base.RelayQuerySender;
import co.fusionx.relay.internal.sender.packet.PacketSender;

@Singleton
public class RelayQueryUserGroup implements QueryUserGroup {

    private final GenericBus<Event> mSessionBus;

    private final PacketSender mSender;

    private final LibraryUser mUser;

    private final ConnectionConfiguration mConfiguration;

    private final Collection<RelayQueryUser> mQueryUsers;

    @Inject
    public RelayQueryUserGroup(final GenericBus<Event> sessionBus, final PacketSender sender,
            final RelayUserChannelGroup group, final ConnectionConfiguration configuration) {
        mSessionBus = sessionBus;
        mSender = sender;
        mUser = group.getUser();
        mConfiguration = configuration;

        mQueryUsers = new LinkedHashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RelayQueryUser> getQueryUsers() {
        return mQueryUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RelayQueryUser> getQueryUser(final String nick) {
        return FluentIterable.from(mQueryUsers)
                .filter(u -> nick.equals(u.getNick().getNickAsString()))
                .first();
    }

    public RelayQueryUser addQueryUser(final String nick) {
        final RelayQuerySender sender = new RelayQuerySender(mSender, mUser, this);
        final RelayQueryUser user = new RelayQueryUser(mSessionBus, mConfiguration, sender, nick);

        // Horrible but has to be done - see the comment on the method
        sender.setQueryUser(user);
        mQueryUsers.add(user);

        return user;
    }

    public void removeQueryUser(final RelayQueryUser user) {
        mQueryUsers.remove(user);
        user.markInvalid();
    }
}