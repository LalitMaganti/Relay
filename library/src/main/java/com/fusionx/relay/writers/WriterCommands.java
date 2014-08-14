package com.fusionx.relay.writers;

public final class WriterCommands {

    public final static String PRIVMSG = "PRIVMSG %1$s :%2$s";

    public final static String PART = "PART %1$s";

    public final static String PART_WITH_REASON = "PART %1$s :%2$s";

    public final static String ACTION = "PRIVMSG %1$s :\u0001ACTION %2$s\u0001";

    public final static String Kick = "KICK %1$s %2$s";

    public final static String KICK_WITH_REASON = "KICK %1$s %2$s :%3$s";

    private WriterCommands() {
    }
}