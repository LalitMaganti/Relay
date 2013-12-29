package com.fusionx.relay.writers;

final class WriterCommands {

    public final static String PRIVMSG = "PRIVMSG %1$s :%2$s";

    public final static String Part = "PART %1$s";

    public final static String PartWithReason = "PART %1$s :%2$s";

    public final static String Action = "PRIVMSG %1$s :\u0001ACTION %2$s\u0001";

    public final static String Kick = "KICK %1$s %2$s";

    public final static String KickWithReason = "KICK %1$s %2$s :%3$s";

    private WriterCommands() {
    }
}