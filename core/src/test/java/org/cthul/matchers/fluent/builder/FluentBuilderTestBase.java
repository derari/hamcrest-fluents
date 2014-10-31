package org.cthul.matchers.fluent.builder;

import java.util.Arrays;
import java.util.List;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentTestBase;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.value.MatchValue;
import org.junit.Test;
import static org.cthul.matchers.fluent.adapters.EachOfAdapter.eachItem;
import static org.hamcrest.Matchers.*;

/**
 *
 */
public abstract class FluentBuilderTestBase extends FluentTestBase {
    
    @Test
    public void test_simple_match_success() {
        fluent(3).__(lessThan(5));
        apply();
        assertSuccess();
    }
    
    @Test
    public void test_chained_match_success() {
        fluent(3)
                .__(lessThan(5))
                .__(greaterThan(0));
        apply();
        assertSuccess();
    }
    
    @Test
    public void test_chained_match_fail() {
        fluent(3)
                .__(lessThan(5))
                .__(greaterThan(4))
                .__(lessThan(2));
        apply();
        assertMismatch("less than <4>");
    }
    
    @Test
    public void test_negated_match_success() {
        fluent(3)
                .not(lessThan(2))
                .__(greaterThan(1));
        apply();
        assertSuccess();
    }
    
    @Test
    public void test_negated_match_fail() {
        fluent(3)
                .not(lessThan(5))
                .__(greaterThan(1));
        apply();
        assertMismatch("less than <5>");
    }
    
    @Test
    public void test_fluent_isA_success() {
        fluent((Object) 3)
                .as(Integer.class)
                .is(lessThan(5));
        apply();
        assertSuccess();
    }
    
    @Test
    public void test_fluent_isA_type_fail() {
        fluent((Object) "")
                .as(Integer.class)
                .is(lessThan(5));
        apply();
        assertMismatch("is an instance of java.lang.Integer",
                       "but: \"\" is a java.lang.String");
    }
    
    @Test
    public void test_fluent_isA_value_fail() {
        fluent((Object) 3)
                .as(Integer.class)
                .is(lessThan(2));
        apply();
        assertMismatch("is a value less than <2>",
                       "was greater than <2>");
    }
    
    @Test
    public void test_property_isA_success() {
        List<Object> list = Arrays.asList((Object) 1, 3, 5);
        fluent(list)
                .__(eachItem()).as(Integer.class)
                        .is(lessThan(6))
                .isNot(empty());
        apply();
        assertSuccess();
    }
    
    @Test
    public void test_property_isA_fail() {
        List<Object> list = Arrays.asList((Object) 1, 3, 5);
        fluent(list)
                .__(eachItem()).as(Integer.class)
                        .is(lessThan(2))
                .isNot(empty());
        apply();
        assertMismatch(
                "a value less than <2>",
                "#1 <3> was greater than <2>");
    }
    
    @Test
    public void test_either_success() {
        fluent(3)
                .is().either(greaterThan(5))
                .or(lessThan(4));
        apply();
        assertSuccess();
    }
    
    @Test
    public void test_either_fail() {
        fluent(3)
                .is().either(greaterThan(5))
                .or(lessThan(3));
        apply();
        assertMismatch(
                "is a value greater than <5> or a value less than <3>",
                "<3> was less than <5> and <3> was equal to <3>");
    }
    
    // -------------------------------------------------------------------------
    
    private MatchValue<?> value = null;

    protected abstract <T> Fluent<T> newFluent(MatchValue<T> t);
    
    protected <T> Fluent<T> fluent(MatchValue<T> t) {
        value = t;
        return newFluent(t);
    }
    
    protected <T> Fluent<T> fluent(T t) {
        return fluent(IdentityValue.value(t));
    }
    
    protected void apply(Object t) {
        apply(IdentityValue.value(t));
    }
    
    protected void apply() {
        apply(value);
    }
    
    protected void apply(MatchValue<?> value) {
    }
    
    
}
