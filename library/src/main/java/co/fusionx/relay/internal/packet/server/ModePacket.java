package co.fusionx.relay.internal.packet.server;

import co.fusionx.relay.internal.packet.Packet;

public class ModePacket implements Packet {

    public final String channelName;

    public final String mode;

    public final String nick;

    public ModePacket(final String channelName, final String mode, final String nick) {
        this.channelName = channelName;
        this.mode = mode;
        this.nick = nick;
    }

    @Override
    public String getLine() {
        return String.format("MODE %s %s %s", channelName, mode, nick);
    }
}