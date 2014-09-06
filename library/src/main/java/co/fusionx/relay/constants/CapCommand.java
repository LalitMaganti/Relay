package co.fusionx.relay.constants;

public enum CapCommand {
    LS("LS"),
    LIST("LIST"),
    REQ("REQ"),
    ACK("ACK"),
    NAK("NAK"),
    CLEAR("CLEAR"),
    END("END");

    private final String mSubCommand;

    CapCommand(final String subCommand) {
        mSubCommand = subCommand;
    }

    public String getSubCommand() {
        return mSubCommand;
    }

    public static CapCommand getCommandFromString(final String string) {
        for (final CapCommand command : CapCommand.values()) {
            if (command.getSubCommand().equals(string)) {
                return command;
            }
        }
        return null;
    }
}