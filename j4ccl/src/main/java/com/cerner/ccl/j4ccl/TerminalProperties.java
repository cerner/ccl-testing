package com.cerner.ccl.j4ccl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Immutable POJO defining execution properties that must be handled by the execution terminal opposed to by CCL. If not
 * explicitly provided default values are assigned for the cclPromptPattern, cclLoginPromptPattern,
 * cclLoginSuccssPromptPattern and cclLoginFailurePatterns. An osPromptPattern must be provided. The constructor will
 * thrown an exception otherwise.
 *
 * @author Fred Eckertson
 *
 */
public class TerminalProperties {
    private static final String DEFAULT_CCL_PROMPT_PATTERN = "\\n\\s*[1-9]\\d*\\)$";
    private static final String DEFAULT_CCL_LOGIN_PROMPT_PATTERN = "\\(Hit PF3 or RETURN to skip security login; this will disable Uar functions\\)";
    private static final String DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN = "Enter Y to continue.*";
    private static final List<String> DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS = Arrays.asList(
            "V500 SECURITY LOGIN FAILURE", "V500 SECURITY LOGIN WARNING", "Retry \\(Y/N\\)", "Repeat New Password:");

    private static TerminalProperties globalTerminalProperties;

    private final boolean skipEnvset;
    private final String osPromptPattern;
    private final String cclPromptPattern;
    private final String cclLoginPromptPattern;
    private final String cclLoginSuccessPromptPattern;
    private final List<String> cclLoginFailurePromptPatterns;
    private final long expectationTimeout;
    private final String logfileLocation;
    private final boolean specifyDebugCcl;

    /**
     * private default constructor to inhibit use.
     */
    private TerminalProperties() {
        this(new TerminalPropertiesBuilder());
    }

    private TerminalProperties(final TerminalPropertiesBuilder builder) {
        skipEnvset = builder.skipEnvset;
        if (builder.osPromptPattern == null || builder.osPromptPattern.isEmpty()) {
            throw new IllegalArgumentException("An OS prompt pattern must be provided");
        }
        osPromptPattern = builder.osPromptPattern;
        cclPromptPattern = builder.cclPromptPattern != null && !builder.cclPromptPattern.isEmpty()
                ? builder.cclPromptPattern : DEFAULT_CCL_PROMPT_PATTERN;
        cclLoginPromptPattern = builder.cclLoginPromptPattern != null && !builder.cclLoginPromptPattern.isEmpty()
                ? builder.cclLoginPromptPattern : DEFAULT_CCL_LOGIN_PROMPT_PATTERN;
        cclLoginSuccessPromptPattern = builder.cclLoginSuccessPromptPattern != null
                && !builder.cclLoginSuccessPromptPattern.isEmpty() ? builder.cclLoginSuccessPromptPattern
                        : DEFAULT_CCL_LOGIN_SUCCESS_PROMPT_PATTERN;
        cclLoginFailurePromptPatterns = builder.cclLoginFailurePromptPatterns != null
                && !builder.cclLoginFailurePromptPatterns.isEmpty()
                        ? Arrays.asList(builder.cclLoginFailurePromptPatterns
                                .toArray(new String[builder.cclLoginFailurePromptPatterns.size()]))
                        : DEFAULT_CCL_LOGIN_FAILURE_PROMPT_PATTERNS;
        expectationTimeout = builder.expectationTimeout;
        logfileLocation = builder.logfileLocation != null ? builder.logfileLocation : "";
        specifyDebugCcl = builder.specifyDebugCcl;
    }

    /**
     * Builder for creating a TermimalProperties instance.
     *
     * @author Fred Eckertson
     *
     */
    public static class TerminalPropertiesBuilder {
        boolean skipEnvset;
        String osPromptPattern;
        String cclPromptPattern;
        String cclLoginPromptPattern;
        String cclLoginSuccessPromptPattern;
        List<String> cclLoginFailurePromptPatterns;
        long expectationTimeout;
        String logfileLocation;
        boolean specifyDebugCcl = true;

        /**
         * Sets the skipEnvset value of this TermimalPropertiesBuilder.
         *
         * @param skipEnvset
         *            The skipEnvset value to set.
         * @return This TermimalPropertiesBuilder instance.
         */
        public TerminalPropertiesBuilder setSkipEnvset(final boolean skipEnvset) {
            this.skipEnvset = skipEnvset;
            return this;
        }

        /**
         * Sets the osPromptPattern value of this TermimalPropertiesBuilder.
         *
         * @param osPromptPattern
         *            The osPromptPattern value to set.
         * @return This TermimalPropertiesBuilder instance.
         */
        public TerminalPropertiesBuilder setOsPromptPattern(final String osPromptPattern) {
            this.osPromptPattern = osPromptPattern;
            return this;
        }

        /**
         * Sets the cclPromptPattern value of this TermimalPropertiesBuilder.
         *
         * @param cclPromptPattern
         *            The cclPromptPattern value to set.
         * @return This TermimalPropertiesBuilder instance.
         */
        public TerminalPropertiesBuilder setCclPromptPattern(final String cclPromptPattern) {
            this.cclPromptPattern = cclPromptPattern;
            return this;
        }

        /**
         * Sets the cclLoginPromptPattern value of this TermimalPropertiesBuilder.
         *
         * @param cclLoginPromptPattern
         *            The cclPromptPattern value to set.
         * @return This TermimalPropertiesBuilder instance.
         */
        public TerminalPropertiesBuilder setCclLoginPromptPattern(final String cclLoginPromptPattern) {
            this.cclLoginPromptPattern = cclLoginPromptPattern;
            return this;
        }

        /**
         * Sets the cclLoginSuccessPromptPattern value of this TermimalPropertiesBuilder.
         *
         * @param cclLoginSuccessPromptPattern
         *            The cclLoginSuccessPromptPattern value to set.
         * @return This TermimalPropertiesBuilder instance.
         */
        public TerminalPropertiesBuilder setCclLoginSuccessPromptPattern(final String cclLoginSuccessPromptPattern) {
            this.cclLoginSuccessPromptPattern = cclLoginSuccessPromptPattern;
            return this;
        }

        /**
         * Sets the cclLoginFailurePromptPatterns value of this TermimalPropertiesBuilder.
         *
         * @param cclLoginFailurePromptPatterns
         *            The cclLoginFailurePromptPattern value to set.
         * @return This TermimalPropertiesBuilder instance.
         */
        public TerminalPropertiesBuilder setCclLoginFailurePromptPatterns(
                final List<String> cclLoginFailurePromptPatterns) {
            this.cclLoginFailurePromptPatterns = cclLoginFailurePromptPatterns;
            return this;
        }

        /**
         * Sets the logfileLocation value for this TermimalPropertiesBuilder.
         *
         * @param logfileLocation
         *            The logfileLocation value to set.
         * @return This TermimalPropertiesBuilder instance.
         */
        public TerminalPropertiesBuilder setLogfileLocation(final String logfileLocation) {
            this.logfileLocation = logfileLocation;
            return this;
        }

        /**
         * Sets the expectationTimeout value for this TermimalPropertiesBuilder.
         *
         * @param expectationTimeout
         *            The expectationTimeout value to set.
         * @return This TermimalPropertiesBuilder instance.
         */
        public TerminalPropertiesBuilder setExpectationTimeout(final long expectationTimeout) {
            this.expectationTimeout = expectationTimeout;
            return this;
        }

        /**
         * Sets the specifyDebugCcl value for this TermimalPropertiesBuilder.
         *
         * @param specifyDebugCcl
         *            The specifyDebugCcl value to set.
         * @return This TermimalPropertiesBuilder instance.
         */
        public TerminalPropertiesBuilder setSpecifyDebugCcl(final boolean specifyDebugCcl) {
            this.specifyDebugCcl = specifyDebugCcl;
            return this;
        }

        /**
         * Generates a new TermimalProperties instance based on the properties of this TermimalPropertiesBuilder.
         *
         * @return A TermimalProperties instance.
         */
        @SuppressWarnings("synthetic-access")
        public TerminalProperties build() {
            return new TerminalProperties(this);
        }
    }

    /**
     * Constructs the default OS prompt pattern corresponding to a host, environment and username, viz.,
     * user:environment@host:[^\r\n]*(\r|\n)+#\s* or user:\w*@host:[^\r\n]*(\r|\n)+#\s* if an environment value is not
     * provided.
     *
     * @param host
     *            The host.
     * @param environment
     *            The environment.
     * @param username
     *            The username.
     * @return The default OS prompt pattern corresponding to host, environment and username.
     */
    public static String constructDefaultOsPromptPattern(final String host, final String environment,
            final String username) {
        return new StringBuilder().append(username).append(":")
                .append(environment != null && !environment.isEmpty() ? environment : "\\w*").append("@").append(host)
                .append(":").append("[^\\r\\n]*(\\r|\\n)+#\\s*").toString();

    }

    /**
     * Obtains a new TermimalPropertiesBuilder instance.
     *
     * @return A new TermimalPropertiesBuilder instance.
     */
    public static TerminalPropertiesBuilder getNewBuilder() {
        return new TerminalPropertiesBuilder();
    }

    /**
     * Retrieves the skipEnvset value of this TerminalProperties instance.
     *
     * @return The skipEnvset value of this TerminalProperties instance.
     */
    public boolean getSkipEnvset() {
        return skipEnvset;
    }

    /**
     * Retrieves the osPromptPattern value of this TerminalProperties instance.
     *
     * @return The osPromptPattern value of this TerminalProperties instance.
     */
    public String getOsPromptPattern() {
        return osPromptPattern;
    }

    /**
     * Retrieves the cclPromptPattern value of this TerminalProperties instance.
     *
     * @return The cclPromptPattern value of this TerminalProperties instance.
     */
    public String getCclPromptPattern() {
        return cclPromptPattern;
    }

    /**
     * Retrieves the cclLoginPromptPattern value of this TerminalProperties instance.
     *
     * @return The cclLoginPromptPattern value of this TerminalProperties instance.
     */
    public String getCclLoginPromptPattern() {
        return cclLoginPromptPattern;
    }

    /**
     * Retrieves the cclLoginSuccessPromptPattern value of this TerminalProperties instance.
     *
     * @return The cclLoginSuccessPromptPattern value of this TerminalProperties instance.
     */
    public String getCclLoginSuccessPromptPattern() {
        return cclLoginSuccessPromptPattern;
    }

    /**
     * Retrieves the cclLoginFailurePromptPatterns for this TerminalProperties instance.
     *
     * @return The cclLoginFailurePromptPatterns for this TerminalProperties instance.
     */
    public List<String> getCclLoginFailurePromptPatterns() {
        return Collections.unmodifiableList(cclLoginFailurePromptPatterns);
    }

    /**
     * Retrieves the expectationTimeout for this TerminalProperties instance.
     *
     * @return The expectationTimeout for this TerminalProperties instance.
     */
    public long getExpectationTimeout() {
        return expectationTimeout;
    }

    /**
     * Retrieves the specifyDebugCcl value of this TerminalProperties instance.
     *
     * @return The specifyDebugCcl value of this TerminalProperties instance.
     */
    public boolean getSpecifyDebugCcl() {
        return specifyDebugCcl;
    }

    /**
     * Retrieves the logfileLocation for this TerminalProperties instance.
     *
     * @return The logfileLocation for this TerminalProperties instance.
     */
    public String getLogfileLocation() {
        return logfileLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final TerminalProperties rhs = (TerminalProperties) obj;
        return new EqualsBuilder().append(skipEnvset, rhs.skipEnvset).append(osPromptPattern, rhs.osPromptPattern)
                .append(cclPromptPattern, rhs.cclPromptPattern).append(cclLoginPromptPattern, rhs.cclLoginPromptPattern)
                .append(cclLoginSuccessPromptPattern, rhs.cclLoginSuccessPromptPattern)
                .append(cclLoginFailurePromptPatterns, rhs.cclLoginFailurePromptPatterns)
                .append(expectationTimeout, rhs.expectationTimeout).append(logfileLocation, rhs.logfileLocation)
                .append(specifyDebugCcl, rhs.specifyDebugCcl).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(skipEnvset).append(osPromptPattern).append(cclPromptPattern)
                .append(cclLoginPromptPattern).append(cclLoginSuccessPromptPattern)
                .append(cclLoginFailurePromptPatterns).append(expectationTimeout).append(logfileLocation)
                .append(specifyDebugCcl).toHashCode();
    }

    /**
     * Sets the global TerminalProperties object provided that it has never been set before. Once set to a non-null
     * value it can never be changed.
     *
     * @param terminalProperties
     *            The new value for the global TerminalProperties object.
     */
    public static void setGlobalTerminalProperties(final TerminalProperties terminalProperties) {
        globalTerminalProperties = terminalProperties;
    }

    /**
     * Retrieves the global TerminalProperties object which could be null if the global one has never been set. TODO:
     * find a better way to communicate the osPromptPattern to the Environment class
     *
     * @return The global TerminalProperties object.
     */
    public static TerminalProperties getGlobalTerminalProperties() {
        return globalTerminalProperties;
    }
}
