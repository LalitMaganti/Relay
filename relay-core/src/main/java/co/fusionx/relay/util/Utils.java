package co.fusionx.relay.util;

import org.apache.commons.lang3.StringUtils;

public class Utils {

    public static String stripColorsFromMessage(final String line) {
        final int length = line.length();
        final StringBuilder buffer = new StringBuilder();
        int i = 0;
        while (i < length) {
            final int start = line.indexOf('\u0003', i);
            if (start < 0) {
                buffer.append(line.substring(i));
                break;
            }
            buffer.append(line.substring(i, start));

            // Advance
            i = start + 1;

            // If out of bounds simply break
            if (i == length) {
                break;
            }

            // Skip "x" or "xy" (foreground color).
            final int firstDigit = Utils.digitValue(line.charAt(i));
            if (firstDigit < 0) {
                continue;
            }

            // Advance
            i++;

            // If out of bounds simply break
            if (i == length) {
                break;
            }

            final int secondDigit = Utils.digitValue(line.charAt(i));
            if (secondDigit >= 0) {
                i++;

                // If out of bounds simply break
                if (i == length) {
                    break;
                }
            }

            // Now skip ",x" or ",xy" (background color).
            char ch = line.charAt(i);

            // If not a comma simply go to the next one
            if (ch != ',') {
                continue;
            }

            // Advance
            i++;

            if (i == length) {
                // Keep the comma
                i--;
            } else {
                final int backFirstDigit = Utils.digitValue(line.charAt(i));
                if (backFirstDigit >= 0) {
                    i++;
                    if (i == length) {
                        break;
                    }
                    final int backSecondDigit = Utils.digitValue(line.charAt(i));
                    if (backSecondDigit >= 0) {
                        i++;
                    }
                    if (i == length) {
                        break;
                    }
                } else {
                    // Keep the comma
                    i--;
                }
            }
        }
        return buffer.toString();
    }

    private static int digitValue(final char character) {
        return Character.isDigit(character) ? Character.getNumericValue(character) : -1;
    }

    public static String returnNonEmpty(final String realName, final String relayUser) {
        return StringUtils.isNotEmpty(realName) ? realName : relayUser;
    }
}