/*
    HoloIRC - an IRC client for Android

    Copyright 2013 Lalit Maganti

    This file is part of HoloIRC.

    HoloIRC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HoloIRC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with HoloIRC. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fusionx.androidirclibrary.parser;

import com.fusionx.androidirclibrary.Server;
import com.fusionx.androidirclibrary.connection.BaseConnection;
import com.fusionx.androidirclibrary.constants.ServerCommands;
import com.fusionx.androidirclibrary.event.ErrorEvent;
import com.fusionx.androidirclibrary.event.Event;
import com.fusionx.androidirclibrary.event.QuitEvent;
import com.fusionx.androidirclibrary.misc.CoreListener;
import com.fusionx.androidirclibrary.util.IRCUtils;
import com.fusionx.androidirclibrary.writers.ServerWriter;

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