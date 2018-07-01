package com.cerner.ccl.cdoc.velocity.navigation;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.cdoc.AbstractBeanUnitTest;

/**
 * Unit tests for {@link Navigation}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
public class NavigationTest extends AbstractBeanUnitTest<Navigation> {
    private final String anchorText = "the anchor text";
    private final String destination = "i am the destination";
    private final Navigation nav = new Navigation(anchorText, destination);

    /**
     * Construction with {@code null} anchor text should fail.
     */
    @Test
    public void testConstructNullAnchorText() {
        expect(IllegalArgumentException.class);
        expect("Anchor text cannot be null.");
        new Navigation(null, destination);
    }

    /**
     * Construction with a {@code null} destination should fail.
     */
    @Test
    public void testConstructNullDestination() {
        expect(IllegalArgumentException.class);
        expect("Destination cannot be null.");
        new Navigation(anchorText, null);
    }

    /**
     * Two navigation objects with different anchor text should be inequal.
     */
    @Test
    public void testEqualsDifferentAnchorText() {
        final Navigation other = new Navigation(StringUtils.reverse(anchorText), destination);
        assertThat(nav).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(nav);
    }

    /**
     * Two navigation objects with different destinations should be inequal.
     */
    @Test
    public void testEqualsDifferentDestinations() {
        final Navigation other = new Navigation(anchorText, StringUtils.reverse(destination));
        assertThat(nav).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(nav);
    }

    /**
     * Test the retrieval of the anchor text.
     */
    @Test
    public void testGetAnchorText() {
        assertThat(nav.getAnchorText()).isEqualTo(anchorText);
    }

    /**
     * Test the retrieval of the destination.
     */
    @Test
    public void testGetDestination() {
        assertThat(nav.getDestination()).isEqualTo(destination);
    }

    @Override
    protected Navigation getBean() {
        return nav;
    }

    @Override
    protected Navigation newBeanFrom(final Navigation otherBean) {
        return new Navigation(otherBean.getAnchorText(), otherBean.getDestination());
    }
}
