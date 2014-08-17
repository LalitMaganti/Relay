package co.fusionx.relay.base;

public interface Conversation {

    public String getId();

    public Server getServer();

    public boolean isValid();
}