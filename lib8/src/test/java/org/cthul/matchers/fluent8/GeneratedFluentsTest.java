package org.cthul.matchers.fluent8;

import org.cthul.matchers.fluent.builder.Matchable;
import org.hamcrest.Matcher;
import static org.hamcrest.MatcherAssert.*;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Test;


/**
 *
 */
public class GeneratedFluentsTest {
    
    Matcher<Object> matcher = null;
    boolean complete = false;
    
    <V> Matchable<V, CompleteFluent> matchable() {
        return m -> {
            matcher = (Matcher) m;
            return new CompleteFluent();
        };
    }
    
    @Test
    public void test_double_matcher() {
        new DoubleFluent.Step<>(matchable())
                .either()
                    .nullValue()
                    .lessThan(3d)
                .or().greaterThan(5d)
            .complete();
        assertThat(complete, Is.is(true));
        assertThat(null, matcher);
        assertThat(2d, matcher);
        assertThat(7d, matcher);
        assertThat(4d, IsNot.not(matcher));
    }
    
    class CompleteFluent {
        void complete() {
            complete = true;
        }
    }
}
