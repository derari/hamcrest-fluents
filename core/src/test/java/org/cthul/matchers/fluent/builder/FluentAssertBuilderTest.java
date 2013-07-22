package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.Fluent;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import org.cthul.matchers.fluent.value.MatchValue;

/**
 *
 */
public class FluentAssertBuilderTest extends FluentBuilderTestBase {

    @Override
    protected <T> Fluent<T> newFluent(MatchValue<T> t) {
        return test_assertThat(t);
    }
    
    @Test
    public void test_message() {
        test_assertThat(3).is(equalTo(1));
        assertMismatch(
                "value is <1>",
                "but was <3>");
    }
    
    @Test
    public void test_message_not() {
        test_assertThat(3).isNot(equalTo(3));
        assertMismatch(
                "value is not <3>",
                "but <3> was <3>");
    }
    
}