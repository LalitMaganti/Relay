package co.fusionx.relay.sender.relay;

public class RelayCtcpCommandSender {

    private final RelayPacketSender mRelayPacketSender;

    public RelayCtcpCommandSender(final RelayPacketSender relayPacketSender) {
        mRelayPacketSender = relayPacketSender;
    }

    public void sendFingerCommand(final String nick) {
    }

    public void sendVersionCommand(final String nick) {
    }

    public void sendErrMsgCommand(final String nick, final String query) {
    }

    public void sendPingCommand(final String nick, final String timestamp) {
    }

    public void sendTimeCommand(final String nick) {
    }
}