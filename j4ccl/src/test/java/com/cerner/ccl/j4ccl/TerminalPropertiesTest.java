package com.cerner.ccl.j4ccl;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit Test for the TerminalProperties class
 *
 * @author Fred Eckertson
 *
 */
public class TerminalPropertiesTest {

    private static Field DEFAULT_CCL_PROMPT_PATTERN;
    private static Field DEFAULT_CCL_LOGIN_PROMPT_PATTERN;
    private static Field DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN;
    private static Field DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS;

    /**
     * One time setup
     *
     * @throws Exception
     *             Not expected.
     */
    @BeforeClass
    public static void beforeClass() throws Exception {
        DEFAULT_CCL_PROMPT_PATTERN = TerminalProperties.class.getDeclaredField("DEFAULT_CCL_PROMPT_PATTERN");
        DEFAULT_CCL_LOGIN_PROMPT_PATTERN = TerminalProperties.class
                .getDeclaredField("DEFAULT_CCL_LOGIN_PROMPT_PATTERN");
        DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN = TerminalProperties.class
                .getDeclaredField("DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN");
        DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS = TerminalProperties.class
                .getDeclaredField("DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS");

        DEFAULT_CCL_PROMPT_PATTERN.setAccessible(true);
        DEFAULT_CCL_LOGIN_PROMPT_PATTERN.setAccessible(true);
        DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.setAccessible(true);
        DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.setAccessible(true);
    }

    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Confirms that the default patterns compile and match things they are expected to match.
     *
     * @throws Exception
     *             Unexpected.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDefaultPatterns() throws Exception {
        Pattern pattern1 = Pattern.compile(
                TerminalProperties.constructDefaultOsPromptPattern("noops", "hillside", "aa111111"), Pattern.MULTILINE);
        assertThat(pattern1.matcher("aa111111:hillside@noops:/home/aa111111\r\n#").matches()).isEqualTo(true);

        Pattern pattern2 = Pattern.compile((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class),
                Pattern.MULTILINE);
        assertThat(pattern2.matcher("\n  0)").matches()).isEqualTo(false);
        assertThat(pattern2.matcher("\n 03)").matches()).isEqualTo(false);
        assertThat(pattern2.matcher("\n076)").matches()).isEqualTo(false);

        assertThat(pattern2.matcher("\n  1)").matches()).isEqualTo(true);
        assertThat(pattern2.matcher("\n 21)").matches()).isEqualTo(true);
        assertThat(pattern2.matcher("\n543)").matches()).isEqualTo(true);
        assertThat(pattern2.matcher("\n1392)").matches()).isEqualTo(true);

        Pattern pattern3 = Pattern.compile((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class),
                Pattern.MULTILINE);
        assertThat(pattern3.matcher("(Hit PF3 or RETURN to skip security login; this will disable Uar functions)")
                .matches()).isEqualTo(true);

        Pattern pattern4 = Pattern.compile(
                (String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class), Pattern.MULTILINE);
        assertThat(pattern4.matcher("Enter Y to continue     Y").matches()).isEqualTo(true);

        Pattern pattern5 = Pattern.compile(
                ((List<String>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class)).get(0),
                Pattern.MULTILINE);
        assertThat(pattern5.matcher("V500 SECURITY LOGIN FAILURE").matches()).isEqualTo(true);

        Pattern pattern6 = Pattern.compile(
                ((List<String>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class)).get(1),
                Pattern.MULTILINE);
        assertThat(pattern6.matcher("V500 SECURITY LOGIN WARNING").matches()).isEqualTo(true);

        Pattern pattern7 = Pattern.compile(
                ((List<String>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class)).get(2),
                Pattern.MULTILINE);
        assertThat(pattern7.matcher("Retry (Y/N)").matches()).isEqualTo(true);

        Pattern pattern8 = Pattern.compile(
                ((List<String>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class)).get(3),
                Pattern.MULTILINE);
        assertThat(pattern8.matcher("Repeat New Password:").matches()).isEqualTo(true);
    }

    /**
     * Confirms that an exception is thrown if an OS prompt pattern is not provided.
     */
    public void testOSPromptPatternRequiredNotNull() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("An OS prompt pattern must be provided");
        TerminalProperties.getNewBuilder().build();
    }

    /**
     * Confirms that an exception is thrown if a non-empty OS prompt pattern is not provided.
     */
    public void testOSPromptPatternRequiredNotEmpty() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("An OS prompt pattern must be provided");
        TerminalProperties.getNewBuilder().setOsPromptPattern("").build();
    }

    /**
     * Confirms that the skipEnvset property is set according to the builder's value and the last set value wins.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testSkipEnvset() throws Exception {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").setSkipEnvset(true);
        TerminalProperties terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(true);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");

        builder.setSkipEnvset(false);
        terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");
    }

    /**
     * Confirms that the opPromptPattern property is set according to the builder's value and the last set value wins.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testOsPromptPattern() throws Exception {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern");
        TerminalProperties terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");

        builder.setOsPromptPattern("osPromptPatternChanged");
        terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPatternChanged");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");
    }

    /**
     * Confirms that the cclPromptPattern property is set according to the builder's value and the last set value wins.
     *
     * @throws Exception
     *             Not Expected
     */
    @Test
    public void testCclPromptPattern() throws Exception {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern");
        TerminalProperties terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern()).isEqualTo("cclPromptPattern");
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");

        builder.setCclPromptPattern("cclPromptPatternChanged");
        terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern()).isEqualTo("cclPromptPatternChanged");
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");
    }

    /**
     * Confirms that the cclLoginPromptPattern property is set according to the builder's value and the last set value
     * wins.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testCclLoginPromptPattern() throws Exception {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").setCclLoginPromptPattern("cclLoginPromptPattern");
        TerminalProperties terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern()).isEqualTo("cclLoginPromptPattern");
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");

        builder.setCclLoginPromptPattern("cclLoginPromptPatternChanged");
        terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern()).isEqualTo("cclLoginPromptPatternChanged");
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");
    }

    /**
     * Confirms that the cclLoginSuccessPromptPattern property is set according to the builder's value and the last set
     * value wins.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testCclLoginSuccessPromptPattern() throws Exception {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern");
        TerminalProperties terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern()).isEqualTo("cclLoginSuccessPromptPattern");
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");

        builder.setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPatternChanged");
        terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo("cclLoginSuccessPromptPatternChanged");
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");
    }

    /**
     * Confirms that the cclLoginFailurPromptPatterns property is set according to the builder's value and the last set
     * value wins.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testCclLoginFailurePromptPattern() throws Exception {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern");
        final List<String> patterns = Arrays.asList(new String[] { "pattern1", "pattern2" });
        builder.setCclLoginFailurePromptPatterns(patterns);
        TerminalProperties terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns()).isEqualTo(patterns);
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");

        final List<String> patternsChanged = Arrays
                .asList(new String[] { "patternChanged1", "patternChanged2", "patternChanged2" });
        builder.setCclLoginFailurePromptPatterns(patternsChanged);
        terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns()).isEqualTo(patternsChanged);
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(0);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");
    }

    /**
     * Confirms that the expectationTimeout property is set according to the builder's value and the last set value
     * wins.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testExpectationTimeout() throws Exception {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").setExpectationTimeout(728);
        TerminalProperties terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(728);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");

        builder.setExpectationTimeout(1962);
        terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(1962);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");
    }

    /**
     * Confirms that the specifyDebugCcl property is set according to the builder's value and the last set value wins.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testSpecifyDebugCcl() throws Exception {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").setSpecifyDebugCcl(false).setExpectationTimeout(728);
        TerminalProperties terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(728);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(false);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");

        builder.setSpecifyDebugCcl(true);
        terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(728);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(true);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("");
    }

    /**
     * Confirms that the logfileLocation property is set according to the builder's value and the last set value wins.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testLogfileLocation() throws Exception {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").setExpectationTimeout(728).setSpecifyDebugCcl(false)
                .setLogfileLocation("logfileLocation.dat");
        TerminalProperties terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(728);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(false);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("logfileLocation.dat");

        builder.setLogfileLocation("logfile.log");
        terminalProperties = builder.build();
        assertThat(terminalProperties.getSkipEnvset()).isEqualTo(false);
        assertThat(terminalProperties.getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminalProperties.getCclPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginSuccessPromptPattern())
                .isEqualTo((String) DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN.get(TerminalProperties.class));
        assertThat(terminalProperties.getCclLoginFailurePromptPatterns())
                .isEqualTo((List<?>) DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS.get(TerminalProperties.class));
        assertThat(terminalProperties.getExpectationTimeout()).isEqualTo(728);
        assertThat(terminalProperties.getSpecifyDebugCcl()).isEqualTo(false);
        assertThat(terminalProperties.getLogfileLocation()).isEqualTo("logfile.log");
    }

    /**
     * Confirms that differing property values lead to differing hash codes by changing one property value at a time and
     * seeing that each change results in a different hash code. To this end, each hash code is added to a set of
     * integers and the size of the set is confirmed to match the number of property changes
     */
    @Test
    public void testHashCode() {
        final Set<Integer> hashCodes = new HashSet<Integer>();
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder();
        builder.setSkipEnvset(true).setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern")
                .setCclLoginPromptPattern("cclLoginPromptPattern")
                .setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern").setCclLoginFailurePromptPatterns(null)
                .setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1", "pattern2" }))
                .setExpectationTimeout(1492).setLogfileLocation("output.log");

        hashCodes.add(builder.build().hashCode());

        builder.setOsPromptPattern("osPromptPattern2");
        hashCodes.add(builder.build().hashCode());

        builder.setCclPromptPattern("cclPromptPattern2");
        hashCodes.add(builder.build().hashCode());

        builder.setCclLoginPromptPattern("cclLoginPromptPattern2");
        hashCodes.add(builder.build().hashCode());

        builder.setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern2");
        hashCodes.add(builder.build().hashCode());

        builder.setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1a", "pattern2a" }));
        hashCodes.add(builder.build().hashCode());

        builder.setSkipEnvset(false);
        hashCodes.add(builder.build().hashCode());

        builder.setExpectationTimeout(999);
        hashCodes.add(builder.build().hashCode());

        builder.setSpecifyDebugCcl(false);
        hashCodes.add(builder.build().hashCode());

        builder.setLogfileLocation("logfile.dat");
        hashCodes.add(builder.build().hashCode());

        assertThat(hashCodes.size()).isEqualTo(10);
    }

    /**
     * Basic test of the equals method.
     */
    @Test
    public void testEquals() {
        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder();
        builder.setSkipEnvset(true).setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern")
                .setCclLoginPromptPattern("cclLoginPromptPattern")
                .setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern").setCclLoginFailurePromptPatterns(null)
                .setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1", "pattern2" }))
                .setExpectationTimeout(2112).setSpecifyDebugCcl(false).setLogfileLocation("output.log");

        final TerminalProperties tp1 = builder.build();

        builder.setOsPromptPattern("osPromptPattern2");
        final TerminalProperties tp2 = builder.build();

        builder.setCclPromptPattern("cclPromptPattern2");
        final TerminalProperties tp3 = builder.build();

        builder.setCclLoginPromptPattern("cclLoginPromptPattern2");
        final TerminalProperties tp4 = builder.build();

        builder.setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern2");
        final TerminalProperties tp5 = builder.build();

        builder.setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1a", "pattern2a" }));
        final TerminalProperties tp6 = builder.build();

        builder.setSkipEnvset(false);
        final TerminalProperties tp7 = builder.build();

        builder.setExpectationTimeout(2525);
        final TerminalProperties tp8 = builder.build();

        builder.setSpecifyDebugCcl(true);
        final TerminalProperties tp9 = builder.build();

        builder.setLogfileLocation("logfile.dat");
        final TerminalProperties tp10 = builder.build();

        assertThat(tp1.equals(tp1)).isEqualTo(true);

        assertThat(tp1.equals(tp2)).isEqualTo(false);
        assertThat(tp1.equals(tp3)).isEqualTo(false);
        assertThat(tp1.equals(tp4)).isEqualTo(false);
        assertThat(tp1.equals(tp5)).isEqualTo(false);
        assertThat(tp1.equals(tp6)).isEqualTo(false);
        assertThat(tp1.equals(tp7)).isEqualTo(false);
        assertThat(tp1.equals(tp8)).isEqualTo(false);
        assertThat(tp1.equals(tp9)).isEqualTo(false);
        assertThat(tp1.equals(tp10)).isEqualTo(false);

        assertThat(tp2.equals(tp3)).isEqualTo(false);
        assertThat(tp2.equals(tp4)).isEqualTo(false);
        assertThat(tp2.equals(tp5)).isEqualTo(false);
        assertThat(tp2.equals(tp6)).isEqualTo(false);
        assertThat(tp2.equals(tp7)).isEqualTo(false);
        assertThat(tp2.equals(tp8)).isEqualTo(false);
        assertThat(tp2.equals(tp9)).isEqualTo(false);
        assertThat(tp2.equals(tp10)).isEqualTo(false);

        assertThat(tp3.equals(tp4)).isEqualTo(false);
        assertThat(tp3.equals(tp5)).isEqualTo(false);
        assertThat(tp3.equals(tp6)).isEqualTo(false);
        assertThat(tp3.equals(tp7)).isEqualTo(false);
        assertThat(tp3.equals(tp8)).isEqualTo(false);
        assertThat(tp3.equals(tp9)).isEqualTo(false);
        assertThat(tp3.equals(tp10)).isEqualTo(false);

        assertThat(tp4.equals(tp5)).isEqualTo(false);
        assertThat(tp4.equals(tp6)).isEqualTo(false);
        assertThat(tp4.equals(tp7)).isEqualTo(false);
        assertThat(tp4.equals(tp8)).isEqualTo(false);
        assertThat(tp4.equals(tp9)).isEqualTo(false);
        assertThat(tp4.equals(tp10)).isEqualTo(false);

        assertThat(tp5.equals(tp6)).isEqualTo(false);
        assertThat(tp5.equals(tp7)).isEqualTo(false);
        assertThat(tp5.equals(tp8)).isEqualTo(false);
        assertThat(tp5.equals(tp9)).isEqualTo(false);
        assertThat(tp5.equals(tp10)).isEqualTo(false);

        assertThat(tp6.equals(tp7)).isEqualTo(false);
        assertThat(tp6.equals(tp8)).isEqualTo(false);
        assertThat(tp6.equals(tp9)).isEqualTo(false);
        assertThat(tp6.equals(tp10)).isEqualTo(false);

        assertThat(tp7.equals(tp8)).isEqualTo(false);
        assertThat(tp7.equals(tp9)).isEqualTo(false);
        assertThat(tp7.equals(tp10)).isEqualTo(false);

        assertThat(tp8.equals(tp9)).isEqualTo(false);
        assertThat(tp8.equals(tp10)).isEqualTo(false);

        assertThat(tp9.equals(tp10)).isEqualTo(false);
    }

    /**
     * Confirm that equals returns false when passed a null reference.
     */
    @Test
    public void testEqualsWithNull() {
        final TerminalProperties terminalProperties = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").build();
        TerminalProperties terminalPropertiesNull = null;
        assertThat(terminalProperties.equals(terminalPropertiesNull)).isFalse();
    }

    /**
     * Confirm that equals returns false when passed something that is not a TerminalProperties.
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEqualsWithNonTerminalProperties() {
        final TerminalProperties terminalProperties = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").build();
        String s = "";
        assertThat(terminalProperties.equals(s)).isFalse();
    }

    /**
     * Confirms that the global terminal properties are initially null until set and can be changed.
     */
    @Test
    public void testGlobalTerminalProperties() {

        final TerminalProperties.TerminalPropertiesBuilder builder = TerminalProperties.getNewBuilder()
                .setSkipEnvset(true).setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern")
                .setCclLoginPromptPattern("cclLoginPromptPattern")
                .setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern").setCclLoginFailurePromptPatterns(null)
                .setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1", "pattern2" }))
                .setExpectationTimeout(1492).setSpecifyDebugCcl(false).setLogfileLocation("output.log");

        final TerminalProperties terminalProperties = builder.build();
        final TerminalProperties terminalPropertiesChanged = builder.setSkipEnvset(false).build();

        TerminalProperties.setGlobalTerminalProperties(terminalProperties);
        assertThat(TerminalProperties.getGlobalTerminalProperties()).isNotNull();
        assertThat(TerminalProperties.getGlobalTerminalProperties().equals(terminalProperties)).isTrue();
        TerminalProperties.setGlobalTerminalProperties(terminalPropertiesChanged);
        assertThat(TerminalProperties.getGlobalTerminalProperties().equals(terminalPropertiesChanged)).isTrue();
    }

    /**
     * Confirms constructDefaultOsPromptPattern behaves as expected.
     */
    @Test
    public void testConstructDefaultOsPromptPattern() {
        String prompt = TerminalProperties.constructDefaultOsPromptPattern("host", "environment", "user");
        assertThat(prompt).isEqualTo("user:environment@host:[^\\r\\n]*(\\r|\\n)+#\\s*");

        prompt = TerminalProperties.constructDefaultOsPromptPattern("host", null, "user");
        assertThat(prompt).isEqualTo("user:\\w*@host:[^\\r\\n]*(\\r|\\n)+#\\s*");

        prompt = TerminalProperties.constructDefaultOsPromptPattern("host", "", "user");
        assertThat(prompt).isEqualTo("user:\\w*@host:[^\\r\\n]*(\\r|\\n)+#\\s*");
    }
}