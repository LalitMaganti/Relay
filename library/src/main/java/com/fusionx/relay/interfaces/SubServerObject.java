package com.fusionx.relay.interfaces;

import com.fusionx.relay.Server;

import java.util.List;

public interface SubServerObject<T> {

    public List<T> getBuffer();

    public String getId();

    public Server getServer();
}