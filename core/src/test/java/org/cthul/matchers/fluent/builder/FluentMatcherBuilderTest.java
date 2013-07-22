package org.cthul.matchers.fluent.builder;

import java.util.Arrays;
import java.util.List;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentMatcher;
import static org.cthul.matchers.fluent.adapters.EachOfAdapter.eachObject;
import org.cthul.matchers.fluent.value.MatchValue;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public class FluentMatcherBuilderTest extends FluentBuilderTestBase {

    private FluentMatcher<?,?> current;
    
    @Override
    protected <T> Fluent<T> newFluent(MatchValue<T> t) {
        FluentMatcher<T,T> m = FluentMatcherBuilder.match();
        current = m;
        return m;
    }

    @Override
    protected void apply(MatchValue value) {
        test_assertThat(value)._(current);
    }
    
    @Test
    public void test_message_with_is() {
        test_assertThat(3, matcherI()
                .is(lessThan(2)));
        assertMismatch("greater than <2>");
    }
    
    @Test
    public void test_property_isA_message() {
        List<Object> list = Arrays.asList((Object) 1, 3, 5);
        fluent(list)
                ._(eachObject()).isA(Integer.class)
                        .that().is(lessThan(2))
                .isNot(empty());
        apply();
        assertMismatch(
                "each is an instance of java.lang.Integer and is a value less than <2>",
                "#1 <3> was greater than <2>");
    }
    
    @Test
    public void test_description() {
        
    }
    
    protected <T> FluentMatcher<T, T> matcher(Class<T> c) {
        return FluentMatcherBuilder.match();
    }
    
    protected FluentMatcher<Integer, Integer> matcherI() {
        return FluentMatcherBuilder.match();
    }
    
}