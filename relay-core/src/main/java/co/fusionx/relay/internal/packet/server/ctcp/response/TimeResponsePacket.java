package co.fusionx.relay.internal.packet.server.ctcp.response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeResponsePacket extends CTCPResponsePacket {

    private static final DateFormat SIMPLE_DATE_FORMAT
            = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public TimeResponsePacket(String nick) {
        super(nick);
    }

    @Override
    public String getResponse() {
        final Date date = new Date();
        return String.format("TIME :%s", SIMPLE_DATE_FORMAT.format(date));
    }
}
