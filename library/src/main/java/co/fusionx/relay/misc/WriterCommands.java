package co.fusionx.relay.misc;

public final class WriterCommands {

    public final static String PRIVMSG = "PRIVMSG %1$s :%2$s";

    public final static String ACTION = "PRIVMSG %1$s :\u0001ACTION %2$s\u0001";

    private WriterCommands() {
    }
}