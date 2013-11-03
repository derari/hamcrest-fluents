package org.cthul.matchers.fluent.value;

import java.util.Arrays;
import java.util.Collection;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.fluent.adapters.AnyOfAdapter;
import org.cthul.matchers.fluent.adapters.EachOfAdapter;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.junit.Test;
import static org.cthul.matchers.CthulMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class MatchValueTest {
    
    public MatchValueTest() {
    }
    
    private MatchValue<Integer> value6 = IdentityValue.value(6);
    private MatchValue<Integer> each456 = EachOfAdapter.eachOf(4, 5, 6);
    private MatchValue<Integer> any167 = AnyOfAdapter.anyOf(1, 6, 7);
    private MatchValue<Integer> size6 = sizeOf(1, 2, 3, 4, 5, 6);
    
    private ElementMatcherWrapper<Integer> gt2_and_lt6 = new ElementMatcherWrapper<>(0, both(greaterThan(2)).and(lessThan(6)));
    private ElementMatcherWrapper<Integer> gt3_or_lt1 = new ElementMatcherWrapper<>(0, either(greaterThan(3)).or(lessThan(1)));

    @Test
    public void test_getValue() {
//        Object value = value6.getValue();
//        assertThat(value, is((Object) value6));
    }
    
    @Test
    public void test_simple_match() {
        MatchResult.Match<?> match = value6.matchResult(gt3_or_lt1).getMatch();
        assertThat(match.toString(), 
                   is("was a value greater than <3>"));
    }
    
    @Test
    public void test_simple_mismatch() {
        MatchResult.Mismatch<?> mismatch = value6.matchResult(gt2_and_lt6).getMismatch();
        assertThat(mismatch.toString(),
                   is("<6> was equal to <6>"));
    }
    
    @Test
    public void test_simple_expected() {
        MatchResult.Mismatch<?> mismatch = value6.matchResult(gt2_and_lt6).getMismatch();
        assertThat(mismatch.getExpectedDescription().toString(),
                   is("a value less than <6>"));
    }
    
    @Test
    public void test_eachOf_match() {
        MatchResult.Match<?> match = each456.matchResult(gt3_or_lt1).getMatch();
        assertThat(match.toString(),
                   is("#0 was a value greater than <3>, #1 was a value greater than <3>, #2 was a value greater than <3>"));
    }
    
    @Test
    public void test_eachOf_mismatch() {
        MatchResult.Mismatch<?> mismatch = each456.matchResult(gt2_and_lt6).getMismatch();
        assertThat(mismatch.toString(),
                   is("#2 <6> was equal to <6>"));
    }
    
    @Test
    public void test_eachOf_expected() {
        MatchResult.Mismatch<?> mismatch = each456.matchResult(gt2_and_lt6).getMismatch();
        assertThat(mismatch.getExpectedDescription().toString(),
                   is("a value less than <6>"));
    }

    @Test
    public void test_anyOf_match() {
        MatchResult.Match<?> match = any167.matchResult(gt3_or_lt1).getMatch();
        assertThat(match.toString(),
                   is("#1 was a value greater than <3>"));
    }
    
    @Test
    public void test_anyOf_mismatch() {
        MatchResult.Mismatch<?> mismatch = any167.matchResult(gt2_and_lt6).getMismatch();
        assertThat(mismatch.toString(),
                   is("#0 <1> was less than <2>, #1 <6> was equal to <6>, and #2 <7> was greater than <6>"));
    }
    
    @Test
    public void test_anyOf_expected() {
        MatchResult.Mismatch<?> mismatch = any167.matchResult(gt2_and_lt6).getMismatch();
        assertThat(mismatch.getExpectedDescription().toString(),
                   is("a value less than <6>"));
    }
    
    @Test
    public void test_size_match() {
        MatchResult.Match<?> match = size6.matchResult(gt3_or_lt1).getMatch();
        assertThat(match.toString(),
                   is("was a value greater than <3>"));
    }
    
    @Test
    public void test_size_mismatch() {
        MatchResult.Mismatch<?> mismatch = size6.matchResult(gt2_and_lt6).getMismatch();
        assertThat(mismatch.toString(),
                   is("<6> was equal to <6>"));
    }
    
    @Test
    public void test_size_expected() {
        MatchResult.Mismatch<?> mismatch = size6.matchResult(gt2_and_lt6).getMismatch();
        assertThat(mismatch.getExpectedDescription().toString(),
                   is("a value less than <6>"));
    }
    
    private static MatchValue<Integer> sizeOf(Object... values) {
        return SIZE_OF.adapt(Arrays.asList(values));
    }
    
    private static final MatchValueAdapter<Collection<?>, Integer> SIZE_OF = new SimpleAdapter<Collection<?>, Integer>("size") {
        @Override
        protected Integer adaptValue(Collection<?> v) {
            return v.size();
        }
    };
}