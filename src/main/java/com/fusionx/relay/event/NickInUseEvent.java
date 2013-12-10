package com.fusionx.relay.event;

import com.fusionx.relay.misc.InterfaceHolders;

public class NickInUseEvent extends ServerEvent {

    public NickInUseEvent() {
        super(InterfaceHolders.getEventResponses().getNickInUserError());
        //super(context.getString(R.string.parser_nick_in_use));
    }
}
