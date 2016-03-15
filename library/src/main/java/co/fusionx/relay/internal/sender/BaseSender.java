package co.fusionx.relay.internal.sender;

import java.io.BufferedWriter;

import co.fusionx.relay.internal.packet.Packet;

public interface BaseSender {

    public void sendPacket(final Packet packet);

    public void onOutputStreamCreated(BufferedWriter writer);

    public void onConnectionTerminated();
}