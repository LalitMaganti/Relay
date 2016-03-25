package co.fusionx.relay.base;

public class FormatSpanInfo {
    public final int start;
    public final int end;

    public enum Format {
        COLOR,
        BOLD,
        ITALIC,
        UNDERLINED
    }

    public enum Color {
        WHITE,
        BLACK,
        BLUE,
        GREEN,
        RED,
        BROWN,
        PURPLE,
        ORANGE,
        YELLOW,
        LIGHT_GREEN,
        TEAL,
        LIGHT_CYAN,
        LIGHT_BLUE,
        PINK,
        GREY,
        LIGHT_GREY
    }

    public final Format format;
    public final Color fgColor;
    public final Color bgColor;

    public FormatSpanInfo(int start, int end, Color fgColor, Color bgColor) {
        this.start = start;
        this.end = end;
        this.format = Format.COLOR;
        this.fgColor = fgColor;
        this.bgColor = bgColor;
    }

    public FormatSpanInfo(int start, int end, Format format) {
        this.start = start;
        this.end = end;
        this.format = format;
        this.fgColor = this.bgColor = null;
    }
}
