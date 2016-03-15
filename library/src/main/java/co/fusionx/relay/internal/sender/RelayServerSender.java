package co.fusionx.relay.internal.sender;

import co.fusionx.relay.internal.packet.server.JoinPacket;
import co.fusionx.relay.internal.packet.server.NickChangePacket;
import co.fusionx.relay.internal.packet.server.RawPacket;
import co.fusionx.relay.internal.packet.server.WhoisPacket;
import co.fusionx.relay.sender.ServerSender;

public class RelayServerSender implements ServerSender {

    private final BaseSender mSender;

    public RelayServerSender(final BaseSender sender) {
        mSender = sender;
    }

    @Override
    public void sendQuery(final String nick, final String message) {
        // This is invalid - we don't have anything to send the server directly
    }

    @Override
    public void sendJoin(final String channelName) {
        mSender.sendPacket(new JoinPacket(channelName));
    }

    @Override
    public void sendNick(final String newNick) {
        mSender.sendPacket(new NickChangePacket(newNick));
    }

    @Override
    public void sendWhois(final String nick) {
        mSender.sendPacket(new WhoisPacket(nick));
    }

    @Override
    public void sendRawLine(final String rawLine) {
        mSender.sendPacket(new RawPacket(rawLine));
    }
}
