package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.ext.ExtensibleFluentAssert;
import org.cthul.matchers.fluent.ext.ExtensionFactory;
import org.cthul.matchers.fluent.ext.FluentFactory;
import org.cthul.matchers.fluent.ext.StepFactory;
import org.cthul.matchers.fluent.value.ElementMatcher;
import org.cthul.matchers.fluent.value.ElementMatcherWrapper;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.cthul.matchers.fluent.value.MatchValueBase;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Implements {@link FluentAssert}.
 * @param <Value> value type
 * @param <This> this type
 */
public class FluentAssertBuilder<Value, This extends FluentAssertBuilder<Value, This>> 
                extends AbstractFluentAssertBuilder<Value, This>
                implements ExtensibleFluentAssert<Value, This> {
    
    @Factory
    public static <T> FluentAssert<T> assertThat(T object) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.instance(), object);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(MatchValue<T> object) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.instance(), object);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(String reason, T object) {
//        return assertThat(object).as(reason);
        return new FluentAssertBuilder<>(reason, AssertionErrorHandler.instance(), object);
    }

    @Factory
    public static <T> FluentAssert<T> assertThat(String reason, MatchValue<T> object) {
//        return assertThat(object).as(reason);
        return new FluentAssertBuilder<>(reason, AssertionErrorHandler.instance(), object);
    }
    
    @Factory
    public static <V, T> FluentAssert<T> assertThat(MatchValueAdapter<? super V, T> adapter, V object) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.instance(), adapter.adapt(object));
    }
    
    @Factory
    public static <V, T> FluentAssert<T> assertThat(V object, MatchValueAdapter<? super V, T> adapter) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.instance(), adapter.adapt(object));
    }
    
    @Factory
    public static <V, T> FluentAssert<T> assertThat(MatchValue<? extends V> object, MatchValueAdapter<? super V, T> adapter) {
        return new FluentAssertBuilder<>(AssertionErrorHandler.instance(), adapter.adapt(object));
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
    private String reason = null;
    private final MatchValue<Value> matchValue;
    private final FailureHandler failureHandler;
    private final Matchable<? extends Value, ?> matchable;

    public FluentAssertBuilder(FailureHandler failureHandler, MatchValue<Value> matchValues) {
        this.matchValue = matchValues;
        this.failureHandler = failureHandler;
        this.matchable = null;
    }

    public FluentAssertBuilder(FailureHandler failureHandler, Value item) {
        this(failureHandler, IdentityValue.value(item));
    }
    
    public FluentAssertBuilder(String reason, FailureHandler failureHandler, MatchValue<Value> matchValues) {
        this(failureHandler, matchValues);
        this.reason = reason;
    }

    public FluentAssertBuilder(String reason, FailureHandler failureHandler, Value item) {
        this(failureHandler, IdentityValue.value(item));
        this.reason = reason;
    }
    
    protected FluentAssertBuilder(Matchable<? extends Value, ?> matchable) {
        this.failureHandler = null;
        this.matchable = matchable;
        this.matchValue = new MatchValueBase<Value>() {
            @Override
            public boolean matches(ElementMatcher<? super Value> matcher) {
                if (!(matcher instanceof ElementMatcherWrapper)) {
                    throw new UnsupportedOperationException("object for internal use only");
                }
                ElementMatcherWrapper<? super Value> w = (ElementMatcherWrapper) matcher;
                FluentAssertBuilder.this.matchable.apply(w.getActualMatcher());
                return true;
            }
            @Override
            public boolean matched() {
                return true;
            }
            @Override
            public MatchResult<?> matchResult() {
                throw new UnsupportedOperationException("object for internal use only");
            }
            @Override
            public void describeValue(Description description) {
                description.appendText(FluentAssertBuilder.this.matchable.toString());
            }
            @Override
            public void describeValueType(Description description) {
                description.appendText("value");
            }
        };
    }

    @Override
    protected void _and() {}
    
    protected void _reason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return (matchable != null ? matchable: matchValue).toString();
    }

    protected FailureHandler _failureHandler() {
        return failureHandler;
    }

    protected MatchValue<Value> _matchValue() {
        return matchValue;
    }
    
    @Override
    protected This _apply(Matcher<? super Value> matcher, String prefix, boolean not) {
        if (matchable != null) {
            matchable.apply(matcher);
        } else {
            ElementMatcher<Value> m = new ElementMatcherWrapper<>(matcherCounter++, matcher, prefix, not);
            if (!matchValue.matches(m)) {
                failureHandler.mismatch(getReason(), matchValue, m);
            }
        }
        return _this();
    }

    @Override
    public <Value2 extends Value> FluentAssert<Value2> isA(Class<Value2> clazz) {
        return (FluentAssert) hasType(clazz);
    }

    @Override
    public <Step> Step as(StepFactory<? super Value, This, Step> adapter) {
        return super.as(adapter);
    }

    private <NextFluent> NextFluent is(FluentFactory<? super Value, NextFluent> adapter) {
        return asFluent(adapter);
    }
    
    public <NextFluent,S> NextFluent as(ExtensionFactory<? super Value, NextFluent, This, S> adapter) {
        return is(adapter.getFluentFactory());
    }
}
