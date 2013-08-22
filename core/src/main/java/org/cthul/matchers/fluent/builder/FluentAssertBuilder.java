package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.FluentPropertyAssert;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.value.ElementMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Implements {@link FluentAssert}.
 */
public class FluentAssertBuilder<Value, This extends FluentAssertBuilder<Value, This>> 
                extends AbstractPropertyAssertBuilder<Value, Value, This, This>
                implements FluentAssert<Value> {
    
    @Factory
    public static <T> FluentAssert<T> assertThat(T object) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.INSTANCE, object);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(MatchValue<T> object) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.INSTANCE, object);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(String reason, T object) {
//        return assertThat(object).as(reason);
        return new FluentAssertBuilder<>(reason, AssertionErrorHandler.INSTANCE, object);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(String reason, MatchValue<T> object) {
//        return assertThat(object).as(reason);
        return new FluentAssertBuilder<>(reason, AssertionErrorHandler.INSTANCE, object);
    }
    
    @Factory
    public static <V, T> FluentAssert<T> assertThat(MatchValueAdapter<V, T> adapter, V object) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.INSTANCE, adapter.adapt(object));
    }
    
    private int matcherCounter = 0;
    private final MatchValue<Value> matchValue;
    private final FailureHandler failureHandler;
    
    public final FluentAssertBuilder<Value, This> and;

    @SuppressWarnings("LeakingThisInConstructor")
    public FluentAssertBuilder(FailureHandler failureHandler, MatchValue<Value> matchValues) {
        this.matchValue = matchValues;
        this.failureHandler = failureHandler;
        this.and = this;
    }

    public FluentAssertBuilder(FailureHandler failureHandler, Value item) {
        this(failureHandler, IdentityValue.value(item));
    }
    
    public FluentAssertBuilder(String reason, FailureHandler failureHandler, MatchValue<Value> matchValues) {
        this(failureHandler, matchValues);
        _as(reason);
    }

    public FluentAssertBuilder(String reason, FailureHandler failureHandler, Value item) {
        this(failureHandler, IdentityValue.value(item));
        _as(reason);
    }
    
    protected void _and() {}

    @Override
    protected This _applyMatcher(Matcher<? super Value> matcher, String prefix, boolean not) {
        MatchValue.ElementMatcher<Value> m = new ElementMatcher<>(matcherCounter++, matcher, prefix, not);
        if (!matchValue.matches(m)) {
            failureHandler.mismatch(getReason(), matchValue, m);
        }
        return _this();
    }

    @Override
    protected This _updateMatcher(Matcher<? super Value> matcher, String prefix, boolean not) {
        // just apply again
        return _applyMatcher(matcher, prefix, not);
    }
    
    @Override
    public This and() {
        _and();
        return _this();
    }

    @Override
    public This and(Matcher<? super Value> matcher) {
        _and();
        return _match(matcher);
    }

    @Override
    public <P> FluentPropertyAssert<Value, P> and(MatchValueAdapter<? super Value, P> adapter) {
        _and();
        return _adapt(adapter);
    }

    @Override
    public <P> FluentPropertyAssert<Value, P> andNot(MatchValueAdapter<? super Value, P> adapter) {
        _and();
        _not();
        return _adapt(adapter);
    }

    @Override
    public <P> FluentAssert<Value> and(MatchValueAdapter<? super Value, P> adapter, Matcher<P> matcher) {
        _and();
        return _match(adapter, matcher);
    }

    @Override
    public <Value2 extends Value> FluentAssert<Value2> isA(Class<Value2> clazz, Matcher<? super Value2> matcher) {
        return (FluentAssert) super.isA(clazz, matcher);
    }

    @Override
    public <Value2 extends Value> FluentPropertyAssert.IsA<Value2, Value2> isA(Class<Value2> clazz) {
        return (FluentPropertyAssert.IsA) super.isA(clazz);
    }

    @Override
    public String toString() {
        return matchValue.toString();
    }
}
