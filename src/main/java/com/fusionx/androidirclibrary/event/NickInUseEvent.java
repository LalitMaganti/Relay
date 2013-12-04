package com.fusionx.androidirclibrary.event;

import com.fusionx.androidirclibrary.misc.InterfaceHolders;

public class NickInUseEvent extends ServerEvent {

    public NickInUseEvent() {
        super(InterfaceHolders.getEventResponses().getNickInUserError());
        //super(context.getString(R.string.parser_nick_in_use));
    }
}
