package co.fusionx.relay.base;

import co.fusionx.relay.event.Event;
import co.fusionx.relay.misc.EventBus;

public interface IRCSession {

    public SessionStatus getStatus();

    public EventBus<Event> getSessionBus();

    public Server getServer();

    public UserChannelDao getUserChannelDao();
}