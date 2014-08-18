package co.fusionx.relay.call.server.cap;

import co.fusionx.relay.call.Call;

public class CAPEndCall implements Call {

    @Override
    public String getLineToSendServer() {
        return "CAP END";
    }
}