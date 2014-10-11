package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import java.util.Collection;
import java.util.LinkedHashSet;

import javax.inject.Inject;

import co.fusionx.relay.core.LibraryUser;
import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.core.Postable;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.internal.sender.RelayQuerySender;
import dagger.Lazy;

public class RelayQueryUserGroup implements InternalQueryUserGroup {

    private final Postable<Event> mSessionBus;

    private final PacketSender mSender;

    private final LibraryUser mUser;

    private final Lazy<InternalServer> mServer;

    private final SessionConfiguration mConfiguration;

    private final Collection<InternalQueryUser> mQueryUsers;

    @Inject
    public RelayQueryUserGroup(final Postable<Event> sessionBus, final Lazy<InternalServer> server,
            final SessionConfiguration configuration, final PacketSender sender,
            final InternalUserChannelGroup group) {
        mSessionBus = sessionBus;
        mServer = server;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public InternalQueryUser getOrAddQueryUser(final String nick) {
        final Optional<InternalQueryUser> optional = getQueryUser(nick);
        if (optional.isPresent()) {
            return optional.get();
        }

        final InternalServer server = mServer.get();
        final InternalQueryUser user = addQueryUser(nick);
        server.postEvent(new NewPrivateMessageEvent(server, user));
        return user;
    }

    @Override
    public void removeQueryUser(final InternalQueryUser user) {
        mQueryUsers.remove(user);
        user.markInvalid();
    }

    private InternalQueryUser addQueryUser(final String nick) {
        final RelayQuerySender sender = new RelayQuerySender(mConfiguration.getSettingsProvider(),
                mSender, mUser, this);
        final InternalQueryUser user = new RelayQueryUser(mSessionBus,
                mConfiguration.getConnectionConfiguration(), sender, nick);

        // Horrible but has to be done - see the comment on the method
        sender.setQueryUser(user);
        mQueryUsers.add(user);
        return user;
    }
}