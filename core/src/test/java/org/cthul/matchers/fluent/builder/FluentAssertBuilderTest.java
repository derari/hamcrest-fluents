package org.cthul.matchers.fluent.builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;

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
                "but: was <3>");
    }
    
    @Test
    public void test_message_not() {
        test_assertThat(3).isNot(equalTo(3));
        assertMismatch(
                "value is not <3>",
                "but: <3> was <3>");
    }
    
    @Test
    public void test_message_with_property() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        test_assertThat(list)
                .has().__(size()).__(lessThan(2));
        assertMismatch("has size a value less than <2>");
    }
    
    private static MatchValueAdapter<Collection<?>, Integer> size() {
        return new SimpleAdapter<Collection<?>, Integer>("size") {
            @Override
            protected Integer adaptValue(Collection<?> value) {
                return value.size();
            }
        };
    }
    
}