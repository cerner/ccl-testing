package com.cerner.ccltesting.maven.ccl.reports;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit Tests for the CerrealReportGenerator class.
 *
 * @author Fred Eckertson
 *
 */
public class CerrealReportGeneratorTest {
    /**
     * Validate the behavior of the stripQuote function.
     */
    @Test
    public void testMaskQuote() {
        String source = "this contains an AtLengthColon quote @41:this string contains forty-one ";
        CerrealReportGenerator.MaskTextResponse response = CerrealReportGenerator.maskText(source);
        assertThat(response.getResponse())
                .isEqualTo("this contains an AtLengthColon quote @41:this string contains forty-one ");
        assertThat(response.recheckLine()).isEqualTo(false);
        assertThat(response.continueLine()).isEqualTo(true);
        source = source + "characters@ man. 'a tick \"|^\"~@27: quote' ~and a tilde quote~ ^then a car";

        response = CerrealReportGenerator.maskText(source);
        assertThat(response.getResponse())
                .isEqualTo("this contains an AtLengthColon quote xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx man"
                        + ". 'a tick \"|^\"~@27: quote' ~and a tilde quote~ ^then a car");
        assertThat(response.recheckLine()).isEqualTo(true);
        assertThat(response.continueLine()).isEqualTo(false);

        response = CerrealReportGenerator.maskText(response.getResponse());
        assertThat(response.getResponse())
                .isEqualTo("this contains an AtLengthColon quote xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx man"
                        + ". xxxxxxxxxxxxxxxxxxxxxxxx ~and a tilde quote~ ^then a car");
        assertThat(response.recheckLine()).isEqualTo(true);
        assertThat(response.continueLine()).isEqualTo(false);

        response = CerrealReportGenerator.maskText(response.getResponse());
        assertThat(response.getResponse())
                .isEqualTo("this contains an AtLengthColon quote xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx man"
                        + ". xxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxx ^then a car");
        assertThat(response.recheckLine()).isEqualTo(true);
        assertThat(response.continueLine()).isEqualTo(false);

        response = CerrealReportGenerator.maskText(response.getResponse());
        assertThat(response.getResponse())
                .isEqualTo("this contains an AtLengthColon quote xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx man"
                        + ". xxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxx ^then a car");
        assertThat(response.recheckLine()).isEqualTo(false);
        assertThat(response.continueLine()).isEqualTo(true);

        source = response.getResponse() + "et @5:'~|\"!@ quote^ and finally |a pipe ";
        response = CerrealReportGenerator.maskText(source);
        assertThat(response.getResponse())
                .isEqualTo("this contains an AtLengthColon quote xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx man"
                        + ". xxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx and finall"
                        + "y |a pipe ");
        assertThat(response.recheckLine()).isEqualTo(true);
        assertThat(response.continueLine()).isEqualTo(false);

        response = CerrealReportGenerator.maskText(response.getResponse());
        assertThat(response.getResponse())
                .isEqualTo("this contains an AtLengthColon quote xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx man"
                        + ". xxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx and finall"
                        + "y |a pipe ");
        assertThat(response.recheckLine()).isEqualTo(false);
        assertThat(response.continueLine()).isEqualTo(true);

        source = response.getResponse() + "'\"@2:^~@ quote|, okay?";
        response = CerrealReportGenerator.maskText(source);
        assertThat(response.getResponse())
                .isEqualTo("this contains an AtLengthColon quote xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx man"
                        + ". xxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx and finall"
                        + "y xxxxxxxxxxxxxxxxxxxxxxx, okay?");
        assertThat(response.recheckLine()).isEqualTo(true);
        assertThat(response.continueLine()).isEqualTo(false);

        response = CerrealReportGenerator.maskText(response.getResponse());
        assertThat(response.recheckLine()).isEqualTo(false);
        assertThat(response.continueLine()).isEqualTo(false);
    }

    /**
     * Verify the behavior of maskQuote when comments are involved
     */
    @Test
    public void testMaskQuoteWithComments() {
        String source = "Here is a line with an in-line comment /*see it?*/ and a final semi-colon comment ;like so";
        CerrealReportGenerator.MaskTextResponse response = CerrealReportGenerator.maskText(source);
        assertThat(response.continueLine()).isEqualTo(false);
        assertThat(response.recheckLine()).isEqualTo(true);
        assertThat(response.getResponse()).isEqualTo(
                "Here is a line with an in-line comment xxxxxxxxxxx and a final semi-colon comment ;like so");
        response = CerrealReportGenerator.maskText(response.getResponse());
        assertThat(response.continueLine()).isEqualTo(false);
        assertThat(response.recheckLine()).isEqualTo(false);
        assertThat(response.getResponse()).isEqualTo(
                "Here is a line with an in-line comment xxxxxxxxxxx and a final semi-colon comment xxxxxxxx");

        source = "Here is a line with an incomplete in-line comment /*starting here and";
        response = CerrealReportGenerator.maskText(source);
        assertThat(response.continueLine()).isEqualTo(true);
        assertThat(response.recheckLine()).isEqualTo(false);
        assertThat(response.getResponse()).isEqualTo(source);

        source = source + " ending here*/ with a final exclamation-delimited comment with continuation !right here\\";
        response = CerrealReportGenerator.maskText(source);
        assertThat(response.continueLine()).isEqualTo(false);
        assertThat(response.recheckLine()).isEqualTo(true);
        assertThat(response.getResponse()).isEqualTo(
                "Here is a line with an incomplete in-line comment xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx with a final exclamation-delimited comment with continuation !right here\\");
        source = response.getResponse();
        response = CerrealReportGenerator.maskText(response.getResponse());
        assertThat(response.continueLine()).isEqualTo(true);
        assertThat(response.recheckLine()).isEqualTo(false);
        assertThat(response.getResponse()).isEqualTo(source);

        source = source + " and finally ending on this line.";
        response = CerrealReportGenerator.maskText(source);
        assertThat(response.continueLine()).isEqualTo(false);
        assertThat(response.recheckLine()).isEqualTo(false);
        assertThat(response.getResponse()).isEqualTo(
                "Here is a line with an incomplete in-line comment xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx with a final exclamation-delimited comment with continuation xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }
}
