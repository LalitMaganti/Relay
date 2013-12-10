package com.fusionx.relay.writers;

final class WriterCommands {

    public final static String PRIVMSG = "PRIVMSG %1$s :%2$s";

    public final static String Part = "PART %1$s";

    public final static String PartWithReason = "PART %1$s :%2$s";

    public final static String WHO = "WHO %1$s";

    public final static String Action = "PRIVMSG %1$s :\u0001ACTION %2$s\u0001";

    private WriterCommands() {
    }
}