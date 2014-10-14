package co.fusionx.relay.internal.base;

import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import javax.inject.Inject;

import co.fusionx.relay.configuration.ConnectionConfiguration;
import co.fusionx.relay.internal.util.SocketUtils;

public class SocketConnection {

    private final ConnectionConfiguration mConnectionConfiguration;

    private Socket mSocket;

    @Inject
    public SocketConnection(final ConnectionConfiguration connectionConfiguration) {
        mConnectionConfiguration = connectionConfiguration;
    }

    public Pair<BufferedReader, BufferedWriter> open() throws IOException {
        mSocket = SocketUtils.openSocketConnection(mConnectionConfiguration);

        // Get the reader and writer for the socket
        final BufferedReader socketReader = SocketUtils.getSocketBufferedReader(mSocket);
        final BufferedWriter socketWriter = SocketUtils.getSocketBufferedWriter(mSocket);

        return Pair.of(socketReader, socketWriter);
    }

    public void close() {
        if (mSocket == null || mSocket.isClosed()) {
            mSocket = null;
            return;
        }

        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = null;
    }
}