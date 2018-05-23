package com.cerner.ccl.parser;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;

/**
 * Skeleton definition of a bean unit test. This provides common tests like equality comparisons.
 * 
 * @author Joshua Hyde
 * 
 * @param <T>
 *            The type of bean to be tested.
 */

public abstract class AbstractBeanUnitTest<T> extends AbstractUnitTest {
    /**
     * A bean should be equal to another bean of the same properties.
     */
    @Test
    public void testEqualsAnother() {
        final T bean = getBean();
        final T otherBean = newBeanFrom(bean);

        assertThat(bean).isEqualTo(otherBean);
        assertThat(otherBean).isEqualTo(bean);
        assertThat(bean.hashCode()).isEqualTo(otherBean.hashCode());
    }

    /**
     * A bean should not be equal to an object of a different class hierarchy.
     */
    @Test
    public void testEqualsNotSameType() {
        assertThat(getBean()).isNotEqualTo(new Object());
    }

    /**
     * A bean should not be equal to {@code null}.
     */
    @Test
    public void testEqualsNull() {
        assertThat(getBean()).isNotEqualTo(null);
    }

    /**
     * A bean should be equal to itself.
     */
    @Test
    public void testEqualsSelf() {
        final T bean = getBean();
        assertThat(bean).isEqualTo(bean);
        assertThat(bean.hashCode()).isEqualTo(bean.hashCode());
    }

    /**
     * Test the {@link Object#toString()} method of the bean.
     */
    @Test
    public void testToString() {
        final T bean = getBean();
        assertThat(bean.toString()).isEqualTo(ToStringBuilder.reflectionToString(bean));
    }

    /**
     * Get the bean to be tested.
     * 
     * @return The bean to be tested.
     */
    protected abstract T getBean();

    /**
     * Create a new bean from the given bean.
     * 
     * @param otherBean
     *            The bean from which the new bean is to be created.
     * @return A new instance of the bean from the given bean. This should be equal such that an
     *         {@link Object#equals(Object) and Object#hashCode()} comparison should indicate they are equal.
     */
    protected abstract T newBeanFrom(T otherBean);
}
