package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.FluentProperty;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.ext.ExtendableFluentAssert;
import org.cthul.matchers.fluent.value.ElementMatcher;
import org.cthul.matchers.fluent.value.ElementMatcherWrapper;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Implements {@link FluentAssert}.
 * @param <Value> base value type
 * @param <This> fluent interface implemented by this class
 */
public class FluentAssertBuilder<Value, This extends FluentAssertBuilder<Value, This>> 
                extends AbstractFluentBuilder<Value, This>
                implements ExtendableFluentAssert<Value, This> {
    
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

    @SuppressWarnings("LeakingThisInConstructor")
    public FluentAssertBuilder(FailureHandler failureHandler, MatchValue<Value> matchValues) {
        this.matchValue = matchValues;
        this.failureHandler = failureHandler;
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
    public <NextProperty> FluentProperty<NextProperty, This> and(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _and();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> FluentProperty<NextProperty, This> andNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _and();
        _not();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> This and(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _and();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> This andNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _and();
        _not();
        return _match(adapter, matcher);
    }

    @Override
    public <Value2 extends Value> FluentAssertBuilder<Value2, ?> as(Class<Value2> clazz) {
        return (FluentAssertBuilder) isA(clazz);
    }

    @Override
    public String toString() {
        return matchValue.toString();
    }
}
