package co.fusionx.relay.internal.base;

import co.fusionx.relay.core.ConnectionConfiguration;
import co.fusionx.relay.core.Nick;
import co.fusionx.relay.bus.GenericBus;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.event.query.QueryEvent;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.sender.QuerySender;

public class RelayQueryUser extends RelayAbstractConversation<QueryEvent>
        implements InternalQueryUser {

    private final ConnectionConfiguration mConfiguration;

    private final QuerySender mQuerySender;

    private final Nick mNick;

    public RelayQueryUser(final GenericBus<Event> bus, final ConnectionConfiguration configuration,
            final QuerySender querySender, final String nick) {
        super(bus);

        mConfiguration = configuration;
        mQuerySender = querySender;

        mNick = new RelayNick(nick);
    }

    @Override
    public String getId() {
        return mNick.getNickAsString();
    }

    @Override
    public Nick getNick() {
        return mNick;
    }

    // Equals and hashcode
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof InternalQueryUser)) {
            return false;
        }

        final RelayQueryUser user = (RelayQueryUser) o;
        return mConfiguration.getTitle().equals(user.mConfiguration.getTitle())
                && mNick.equals(user.mNick);
    }

    @Override
    public int hashCode() {
        int result = mConfiguration.getTitle().hashCode();
        result = 31 * result + mNick.hashCode();
        return result;
    }

    // QuerySender interface
    @Override
    public void sendAction(final String action) {
        mQuerySender.sendAction(action);
    }

    @Override
    public void sendMessage(final String message) {
        mQuerySender.sendMessage(message);
    }

    @Override
    public void close() {
        mQuerySender.close();
    }
}
