package co.fusionx.relay.call.server.cap;

import co.fusionx.relay.call.Call;

public class CAPRequestSASLCall implements Call {

    @Override
    public String getLineToSendServer() {
        return "CAP REQ : sasl multi-prefix";
    }
}