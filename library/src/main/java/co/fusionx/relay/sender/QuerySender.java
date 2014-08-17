package co.fusionx.relay.sender;

public interface QuerySender {

    public void sendAction(final String action);

    public void sendMessage(final String message);

    public void close();
}