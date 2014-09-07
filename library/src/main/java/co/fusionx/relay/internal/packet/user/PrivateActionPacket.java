package co.fusionx.relay.internal.packet.user;

import co.fusionx.relay.internal.packet.Packet;

public class PrivateActionPacket implements Packet {

    public final String userNick;

    public final String message;

    public PrivateActionPacket(String userNick, String message) {
        this.userNick = userNick;
        this.message = message;
    }

    @Override
    public String getLine() {
        return String.format("PRIVMSG %1$s :\u0001ACTION %2$s\u0001", userNick, message);
    }
}