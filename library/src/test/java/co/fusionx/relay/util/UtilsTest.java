package co.fusionx.relay.util;

import android.util.Pair;
import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.base.FormatSpanInfo.Color;
import co.fusionx.relay.base.FormatSpanInfo.Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static co.fusionx.relay.util.Utils.parseAndStripColorsFromMessage;
import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class UtilsTest {
    private static final char IRC_BOLD = '\u0002';
    private static final char IRC_COLOR = '\u0003';
    private static final char IRC_ITALIC = '\u001d';
    private static final char IRC_UNDERLINE = '\u001f';
    private static final char IRC_RESET_FORMATTING = '\u000f';

    @Test
    public void parseAndStripColorsFromMessage_overlappingFormatting() throws Exception {
        String testMessage = String.format(
                "%1$sTest%1$s %2$sM%3$ses%2$ss%3$sage",
                IRC_BOLD,
                IRC_UNDERLINE,
                IRC_ITALIC);
        Pair<String, List<FormatSpanInfo>> actual = parseAndStripColorsFromMessage(testMessage);

        assertThat(actual.first).isEqualTo("Test Message");
        assertThat(actual.second).containsOnly(
                new FormatSpanInfo(0, 4, Format.BOLD),
                new FormatSpanInfo(5, 8, Format.UNDERLINED),
                new FormatSpanInfo(6, 9, Format.ITALIC));
    }

    @Test
    public void parseAndStripColorsFromMessage_colorCodes() throws Exception {
        String testMessage = String.format(
                "T%1$s3,5es%1$s03,05t%1$s Me%1$s3s%1$s05sa%1$sge%1$s",
                IRC_COLOR);
        Pair<String, List<FormatSpanInfo>> actual = parseAndStripColorsFromMessage(testMessage);
        assertThat(actual.first).isEqualTo("Test Message");
        assertThat(actual.second).containsOnly(
                new FormatSpanInfo(1, 3, Color.GREEN, Color.BROWN),
                new FormatSpanInfo(3, 4, Color.GREEN, Color.BROWN),
                new FormatSpanInfo(7, 8, Color.GREEN, null),
                new FormatSpanInfo(8, 10, Color.BROWN, null));
    }

    @Test
    public void parseAndStripColorsFromMessage_reset() throws Exception {
        String testMessage = String.format(
                "%sTe%sst%s Message",
                IRC_BOLD,
                IRC_UNDERLINE,
                IRC_RESET_FORMATTING);
        Pair<String, List<FormatSpanInfo>> actual = parseAndStripColorsFromMessage(testMessage);
        assertThat(actual.first).isEqualTo("Test Message");
        assertThat(actual.second).containsOnly(
                new FormatSpanInfo(0, 4, Format.BOLD),
                new FormatSpanInfo(2, 4, Format.UNDERLINED));
    }

}