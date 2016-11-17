package it.alessioricco.btc.util;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowResources;

import it.alessioricco.btc.BuildConfig;
import it.alessioricco.btc.TestEnvironment;
import it.alessioricco.btc.utils.StringUtils;

import static org.assertj.core.api.Java6Assertions.assertThat;


@Config(shadows = { ShadowResources.class },
        sdk = TestEnvironment.sdk,
        constants = BuildConfig.class,
        manifest = TestEnvironment.manifest)
@RunWith(CustomRobolectricTestRunner.class)
public class TestStringUtils {

    @Test
    public void testIsNullOrEmpty() throws Exception {
        final String nullString = null;
        assertThat(StringUtils.isNullOrEmpty(nullString)).isTrue();

        final String emptyString = "";
        assertThat(StringUtils.isNullOrEmpty(emptyString)).isTrue();

        final String nonEmptyString = " ";
        assertThat(StringUtils.isNullOrEmpty(nonEmptyString)).isFalse();
    }

    @Test
    public void testFirstLineOf() throws Exception {
        final String lines = "alfa\nbeta\ngamma\ndelta";
        assertThat(StringUtils.firstLineOf(lines)).isEqualTo("alfa");

        final String emptyString = "";
        assertThat(StringUtils.firstLineOf(emptyString)).isEqualTo("");

        final String nullString = null;
        assertThat(StringUtils.firstLineOf(nullString)).isEqualTo("");
    }

    @Test
    public void testRemoveHtmlTags() throws Exception {

        assertThat(StringUtils.removeHtmlTags("<tag>notag</tag>")).isEqualTo("notag");
        assertThat(StringUtils.removeHtmlTags("<tag></tag>")).isEqualTo("");
        assertThat(StringUtils.removeHtmlTags("")).isEqualTo("");
        assertThat(StringUtils.removeHtmlTags(null)).isEqualTo("");
    }

    @Test
    public void testFormatRSSDate() throws Exception {
        assertThat(StringUtils.formatRSSDate("")).isEmpty();
        assertThat(StringUtils.formatRSSDate(null)).isEmpty();
        assertThat(StringUtils.formatRSSDate(" ")).isEqualTo(" ");
        assertThat(StringUtils.formatRSSDate("foo")).isEqualTo("foo");
        assertThat(StringUtils.formatRSSDate("Tue, 15 Nov 2016 22:00:06 +0000")).isEqualTo("Tue, 15 Nov 2016 22:00");
    }
}
