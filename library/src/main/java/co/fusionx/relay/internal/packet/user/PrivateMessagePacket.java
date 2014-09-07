package co.fusionx.relay.internal.packet.user;

import co.fusionx.relay.internal.packet.Packet;

public class PrivateMessagePacket implements Packet {

    public final String userNick;

    public final String message;

    public PrivateMessagePacket(String userNick, String message) {
        this.userNick = userNick;
        this.message = message;
    }

    @Override
    public String getLine() {
        return String.format("PRIVMSG %1$s :%2$s", userNick, message);
    }
}