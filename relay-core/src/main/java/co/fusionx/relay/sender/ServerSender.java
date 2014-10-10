package co.fusionx.relay.sender;

public interface ServerSender {

    public void sendJoin(final String channelName);

    public void sendNick(final String newNick);

    public void sendWhois(final String nick);

    public void sendRawLine(final String rawLine);
}