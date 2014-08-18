package co.fusionx.relay.call.server;

import co.fusionx.relay.call.Call;

public class ModeCall implements Call {

    public final String channelName;

    public final String mode;

    public final String nick;

    public ModeCall(final String channelName, final String mode, final String nick) {
        this.channelName = channelName;
        this.mode = mode;
        this.nick = nick;
    }

    @Override
    public String getLineToSendServer() {
        return "MODE " + channelName + " " + mode + " " + nick;
    }
}