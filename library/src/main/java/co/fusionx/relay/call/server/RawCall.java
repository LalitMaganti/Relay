package co.fusionx.relay.call.server;

import co.fusionx.relay.call.Call;

public class RawCall implements Call {

    public final String rawLine;

    public RawCall(String rawLine) {
        this.rawLine = rawLine;
    }

    @Override
    public String getLineToSendServer() {
        return rawLine;
    }
}