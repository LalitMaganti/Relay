package co.fusionx.relay.call.server.cap;

import co.fusionx.relay.call.Call;

public class CAPRequestPlainSaslAuthCall implements Call {

    @Override
    public String getLineToSendServer() {
        return "AUTHENTICATE PLAIN";
    }
}