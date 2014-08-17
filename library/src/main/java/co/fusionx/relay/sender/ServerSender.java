package co.fusionx.relay.sender;

public interface ServerSender {

    public void sendQuery(final String nick, final String message);

    public void sendJoin(final String channelName);

    public void sendNick(final String newNick);

    public void sendWhois(final String nick);

    public void sendRawLine(final String rawLine);
}