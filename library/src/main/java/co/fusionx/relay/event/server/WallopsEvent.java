package co.fusionx.relay.event.server;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.base.Server;

public class WallopsEvent extends ServerEvent {

    public final String message;
    public final List<FormatSpanInfo> formats;

    public final String nick;

    public WallopsEvent(final Server server, final String message,
            final List<FormatSpanInfo> formats, final String nick) {
        super(server);

        this.message = message;
        this.formats = formats;
        this.nick = nick;
    }
}
