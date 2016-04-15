package co.fusionx.relay.util;

import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;

public class Utils {

    private static final char IRC_BOLD = '\u0002';
    private static final char IRC_COLOR = '\u0003';
    private static final char IRC_ITALIC = '\u001d';
    private static final char IRC_UNDERLINE = '\u001f';
    private static final char IRC_RESET_FORMATTING = '\u000f';

    public static boolean isNotEmpty(final CharSequence cs) {
        return !TextUtils.isEmpty(cs);
    }

    private static class FormatState {
        private static final int NOT_OPEN = -1;

        private List<FormatSpanInfo> mFormats;
        private int mBoldStart = NOT_OPEN, mColorStart = NOT_OPEN;
        private int mItalicStart = NOT_OPEN, mUnderlineStart = NOT_OPEN;
        FormatSpanInfo.Color mFgColor = null, mBgColor = null;

        void bold(int pos) {
            if (mBoldStart >= 0 && mBoldStart < pos) {
                addBold(mBoldStart, pos);
                mBoldStart = NOT_OPEN;
            } else {
                mBoldStart = pos;
            }
        }

        void italic(int pos) {
            if (mItalicStart >= 0 && mItalicStart < pos) {
                addItalic(mItalicStart, pos);
                mItalicStart = NOT_OPEN;
            } else {
                mItalicStart = pos;
            }
        }

        void underline(int pos) {
            if (mUnderlineStart >= 0 && mUnderlineStart < pos) {
                addUnderline(mUnderlineStart, pos);
                mUnderlineStart = NOT_OPEN;
            } else {
                mUnderlineStart = pos;
            }
        }

        void color(int pos, FormatSpanInfo.Color fg, FormatSpanInfo.Color bg) {
            if (mColorStart >= 0 && pos > 0 && mFgColor != null) {
               addColor(mColorStart, pos, mFgColor, mBgColor);
            }
            mColorStart = pos;
            mFgColor = fg;
            mBgColor = bg;
        }

        void applyAndReset(int pos) {
            if (pos == 0) {
                return;
            }
            if (mColorStart >= 0 && mColorStart < pos && mFgColor != null) {
                addColor(mColorStart, pos, mFgColor, mBgColor);
            }
            if (mBoldStart >= 0 && mBoldStart < pos) {
                addBold(mBoldStart, pos);
            }
            if (mItalicStart >= 0 && mItalicStart < pos) {
                addItalic(mItalicStart, pos);
            }
            if (mUnderlineStart >= 0 && mUnderlineStart < pos) {
                addUnderline(mUnderlineStart, pos);
            }

            mColorStart = mBoldStart = mItalicStart = mUnderlineStart = NOT_OPEN;
        }

        private void addBold(int start, int end) {
            addFormat(new FormatSpanInfo(start, end, FormatSpanInfo.Format.BOLD));
        }

        private void addItalic(int start, int end) {
            addFormat(new FormatSpanInfo(start, end, FormatSpanInfo.Format.ITALIC));
        }

        private void addUnderline(int start, int end) {
            addFormat(new FormatSpanInfo(start, end, FormatSpanInfo.Format.UNDERLINED));
        }

        private void addColor(int start, int end, FormatSpanInfo.Color fgColor,
                              FormatSpanInfo.Color bgColor) {
            addFormat(new FormatSpanInfo(start, end, fgColor, bgColor));
        }

        private void addFormat(FormatSpanInfo info) {
            if (mFormats == null) {
                mFormats = new ArrayList<>();
            }
            mFormats.add(info);
        }
    }

    public static Pair<String, List<FormatSpanInfo>> parseAndStripColorsFromMessage(
            final String line) {
        final FormatState state = new FormatState();
        final int length = line.length();
        final StringBuilder buffer = new StringBuilder();
        int i = 0;


        while (i < length) {
            char c = line.charAt(i++);

            if (c == IRC_BOLD) {
                state.bold(buffer.length());
            } else if (c == IRC_COLOR) {
                int firstFgDigit = digitValue(line, i);
                int secondFgDigit = digitValue(line, i + 1);
                // advance over read digits
                if (firstFgDigit >= 0) {
                    i++;
                }
                if (secondFgDigit >= 0) {
                    i++;
                }

                FormatSpanInfo.Color fg = makeColor(firstFgDigit, secondFgDigit);
                FormatSpanInfo.Color bg = null;
                // check for presence of comma and at least one character after it
                // (if there is no additional character, the comma doesn't belong to the color)
                if ((i + 1) < line.length() && line.charAt(i) == ',') {
                    // advance over comma
                    i++;

                    int firstBgDigit = digitValue(line, i);
                    int secondBgDigit = digitValue(line, i + 1);

                    // advance over read digits
                    if (firstBgDigit >= 0) {
                        i++;
                        if (secondBgDigit >= 0) {
                            i++;
                        }
                        bg = makeColor(firstBgDigit, secondBgDigit);
                    } else {
                        // the character after the comma wasn't numeric, so rewind
                        // to include the comma as regular character
                        i--;
                    }
                }
                state.color(buffer.length(), fg, bg);
            } else if (c == IRC_ITALIC) {
                state.italic(buffer.length());
            } else if (c == IRC_UNDERLINE) {
                state.underline(buffer.length());
            } else if (c == IRC_RESET_FORMATTING) {
                state.applyAndReset(buffer.length());
            } else {
                buffer.append(c);
            }
        }
        state.applyAndReset(buffer.length());
        return Pair.create(buffer.toString(), state.mFormats);
    }

    private static FormatSpanInfo.Color makeColor(int firstDigit, int secondDigit) {
        if (firstDigit < 0) {
            return null;
        }
        int colorValue = secondDigit >= 0 ? (firstDigit * 10 + secondDigit) : firstDigit;

        switch (colorValue) {
            case 0: return FormatSpanInfo.Color.WHITE;
            case 1: return FormatSpanInfo.Color.BLACK;
            case 2: return FormatSpanInfo.Color.BLUE;
            case 3: return FormatSpanInfo.Color.GREEN;
            case 4: return FormatSpanInfo.Color.RED;
            case 5: return FormatSpanInfo.Color.BROWN;
            case 6: return FormatSpanInfo.Color.PURPLE;
            case 7: return FormatSpanInfo.Color.ORANGE;
            case 8: return FormatSpanInfo.Color.YELLOW;
            case 9: return FormatSpanInfo.Color.LIGHT_GREEN;
            case 10: return FormatSpanInfo.Color.TEAL;
            case 11: return FormatSpanInfo.Color.LIGHT_CYAN;
            case 12: return FormatSpanInfo.Color.LIGHT_BLUE;
            case 13: return FormatSpanInfo.Color.PINK;
            case 14: return FormatSpanInfo.Color.GREY;
            case 15: return FormatSpanInfo.Color.LIGHT_GREY;
            default: return null;
        }
    }

    private static int digitValue(final String line, final int pos) {
        if (pos >= line.length()) {
            return -1;
        }
        char c = line.charAt(pos);
        return Character.isDigit(c) ? Character.getNumericValue(c) : -1;
    }

    public static String returnNonEmpty(final String first, final String second) {
        return Utils.isNotEmpty(first) ? first : second;
    }
}