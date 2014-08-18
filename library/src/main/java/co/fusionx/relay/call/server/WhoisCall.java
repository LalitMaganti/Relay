package co.fusionx.relay.call.server;

import co.fusionx.relay.call.Call;

public class WhoisCall implements Call {

    public final String nick;

    public WhoisCall(String nick) {
        this.nick = nick;
    }

    @Override
    public String getLineToSendServer() {
        return "WHOIS " + nick;
    }
}