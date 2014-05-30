package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.FluentPropertyAssert;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.value.*;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Implements {@link FluentAssert}.
 * @param <Value> base value type
 * @param <This> fluent interface implemented by this class
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
    public static <V, T> FluentAssert<T> assertThat(MatchValueAdapter<? super V, T> adapter, V object) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.INSTANCE, adapter.adapt(object));
    }
    
    @Factory
    public static <V, T> FluentAssert<T> assertThat(V object, MatchValueAdapter<? super V, T> adapter) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.INSTANCE, adapter.adapt(object));
    }
    
    @Factory
    public static <V, T> FluentAssert<T> assertThat(MatchValue<? extends V> object, MatchValueAdapter<? super V, T> adapter) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.INSTANCE, adapter.adapt(object));
    }
    
    @Factory
    public static <T> FluentAssert<T> assertThat(T object, Matcher<? super T> matcher) {
        return assertThat(object).__(matcher);
    }
    
    @Factory
    public static <T, P> FluentAssert<T> assertThat(T object, MatchValueAdapter<? super T, P> adapter, Matcher<? super P> matcher) {
        return assertThat(object).__(adapter, null);
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
        ElementMatcher<Value> m = new ElementMatcherWrapper<>(matcherCounter++, matcher, prefix, not);
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
    public This andNot(Matcher<? super Value> matcher) {
        _and();
        _not();
        return _match(matcher);
    }

    @Override
    public This and(Value value) {
        _and();
        return _match(value);
    }

    @Override
    public This andNot(Value value) {
        _and();
        _not();
        return _match(value);
    }

    @Override
    public <NextProperty> FluentPropertyAssert<Value, NextProperty> and(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _and();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> FluentPropertyAssert<Value, NextProperty> andNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _and();
        _not();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> FluentAssert<Value> and(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _and();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> FluentAssert<Value> andNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _and();
        _not();
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
