package co.fusionx.relay.base;

import co.fusionx.relay.event.Event;
import co.fusionx.relay.misc.GenericBus;

public interface IRCSession {

    public SessionStatus getStatus();

    public GenericBus<Event> getSessionBus();

    public Server getServer();

    public UserChannelDao getUserChannelDao();
}