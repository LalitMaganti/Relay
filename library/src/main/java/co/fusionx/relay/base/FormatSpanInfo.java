package co.fusionx.relay.base;

import com.google.common.base.MoreObjects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormatSpanInfo that = (FormatSpanInfo) o;

        if (start != that.start) return false;
        if (end != that.end) return false;
        if (format != that.format) return false;
        if (fgColor != that.fgColor) return false;
        return bgColor == that.bgColor;

    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        result = 31 * result + format.hashCode();
        result = 31 * result + (fgColor != null ? fgColor.hashCode() : 0);
        result = 31 * result + (bgColor != null ? bgColor.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("start", start)
                .add("end", end)
                .add("format", format)
                .add("fgColor", fgColor)
                .add("bgColor", bgColor)
                .toString();
    }
}
