package co.fusionx.relay.call.server;

import co.fusionx.relay.call.Call;

public class JoinCall extends Call {

    private final String channelName;

    public JoinCall(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String getLineToSendServer() {
        return "JOIN " + channelName;
    }
}