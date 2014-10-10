package co.fusionx.relay.constant;

public enum ChannelType {
    SECRET,
    PUBLIC,
    PRIVATE;

    public static ChannelType fromString(final String typeString) {
        switch (typeString) {
            case "@":
                return SECRET;
            case "*":
                return PRIVATE;
            case "=":
                return PUBLIC;
        }
        return null;
    }
}