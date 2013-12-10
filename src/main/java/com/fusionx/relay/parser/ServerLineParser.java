package com.fusionx.relay.parser;

import com.fusionx.relay.Server;
import com.fusionx.relay.connection.BaseConnection;
import com.fusionx.relay.constants.ServerCommands;
import com.fusionx.relay.event.ErrorEvent;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.event.QuitEvent;
import com.fusionx.relay.misc.CoreListener;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.writers.ServerWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ServerLineParser {

    private final Server mServer;

    private final BaseConnection mBaseConnection;

    private final ServerCodeParser mCodeParser;

    private final ServerCommandParser mCommandParser;

    public ServerLineParser(final Server server, final BaseConnection connection) {
        mServer = server;
        mBaseConnection = connection;
        mCommandParser = new ServerCommandParser(this);
        mCodeParser = new ServerCodeParser(this);
    }

    /**
     * A loop which reads each line from the server as it is received and passes it on to parse
     *
     * @param reader - the reader associated with the server stream
     */
    public void parseMain(final BufferedReader reader, final ServerWriter writer)
            throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !mBaseConnection.isUserDisconnected()) {
            final Event quit = parseLine(line, writer);
            if (quit instanceof QuitEvent || quit instanceof ErrorEvent) {
                return;
            }
        }
    }

    /**
     * Parses a line from the server
     *
     * @param rawLine - the raw line from the server
     * @param writer  -
     * @return - returns a boolean which indicates whether the server has disconnected
     */
    Event parseLine(final String rawLine, final ServerWriter writer) {
        final ArrayList<String> parsedArray = IRCUtils.splitRawLine(rawLine, true);
        String s = parsedArray.get(0);
        if (s.equals(ServerCommands.Ping)) {// Immediately return
            final String source = parsedArray.get(1);
            CoreListener.respondToPing(writer, source);
            return new Event(rawLine);
        } else if (s.equals(ServerCommands.Error)) {
            // We are finished - the server has kicked us
            // out for some reason
            return new ErrorEvent(rawLine);
        } else {// Check if the second thing is a code or a command
            if (StringUtils.isNumeric(parsedArray.get(1))) {
                return mCodeParser.parseCode(parsedArray, rawLine);
            } else {
                return mCommandParser.parseCommand(parsedArray, rawLine);
            }
        }
    }

    Server getServer() {
        return mServer;
    }
}