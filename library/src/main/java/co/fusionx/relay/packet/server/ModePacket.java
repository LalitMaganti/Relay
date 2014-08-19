package co.fusionx.relay.packet.server;

import co.fusionx.relay.packet.Packet;

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
    public String getLineToSendServer() {
        return "MODE " + channelName + " " + mode + " " + nick;
    }
}