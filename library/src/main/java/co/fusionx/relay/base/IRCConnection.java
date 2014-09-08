package co.fusionx.relay.base;

import co.fusionx.relay.event.Event;
import co.fusionx.relay.misc.EventBus;

public interface IRCConnection {

    public ConnectionStatus getStatus();

    public EventBus<Event> getSuperBus();

    public Server getServer();

    public UserChannelDao getUserChannelDao();
}