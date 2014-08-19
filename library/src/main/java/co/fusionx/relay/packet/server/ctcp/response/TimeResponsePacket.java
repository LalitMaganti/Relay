package co.fusionx.relay.packet.server.ctcp.response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.fusionx.relay.packet.Packet;

public class TimeResponsePacket implements Packet {

    private final String mRecipient;

    public TimeResponsePacket(String nick) {
        mRecipient = nick;
    }

    @Override
    public String getLineToSendServer() {
        final Date date = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return String.format("NOTICE %s \u0001TIME :%s\u0001", mRecipient,
                dateFormat.format(date));
    }
}
