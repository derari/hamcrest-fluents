package org.cthul.matchers.fluent.builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentMatcher;
import static org.cthul.matchers.fluent.adapters.EachOfAdapter.eachItem;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
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
        reset();
        test_assertThat(value).__(current);
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
                .__(eachItem()).as(Integer.class)
                        .is(lessThan(2))
                .isNot(empty());
        apply();
        assertMismatch(
                "each is a value less than <2>",
                ": #1 <3> was greater than <2>");
    }
    
    @Test
    public void test_isA_property() {
        current = matcher(Object.class)
                .as(List.class).has(size).__(lessThan(3))
                .or().is(0);
        
        
        apply(Arrays.asList(1, 2));
        assertSuccess();
        
        apply(0);
        assertSuccess();
        
        apply(Arrays.asList(1, 2, 3));
        assertMismatch(
                "Expected: list has size a value less than <3> or is <0>",
                "<3> was equal to <3> and was <[1, 2, 3]>");
        
        apply(2);
        assertMismatch("<2> is a java.lang.Integer and was <2>");
    }
    
    protected <T> FluentMatcher<T, T> matcher(Class<T> c) {
        return FluentMatcherBuilder.match();
    }
    
    protected FluentMatcher<Integer, Integer> matcherI() {
        return FluentMatcherBuilder.match();
    }
    
    static final MatchValueAdapter<Collection, Integer> size = new SimpleAdapter<Collection, Integer>("size") {  
        @Override
        protected Integer adaptValue(Collection v) {
            return v.size();
        }
    };
    
}