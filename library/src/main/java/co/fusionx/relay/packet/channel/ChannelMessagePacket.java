package co.fusionx.relay.packet.channel;

import co.fusionx.relay.packet.Packet;
import co.fusionx.relay.misc.WriterCommands;

public class ChannelMessagePacket implements Packet {

    private final String channelName;

    private final String message;

    public ChannelMessagePacket(String channelName, String message) {
        this.channelName = channelName;
        this.message = message;
    }

    @Override
    public String getLine() {
        return String.format(WriterCommands.PRIVMSG, channelName, message);
    }
}