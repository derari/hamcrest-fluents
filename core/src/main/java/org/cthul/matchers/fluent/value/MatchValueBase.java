package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.SelfDescribingBase;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Default superclass for {@link MatchValue}.
 * Provides default implementations for all methods 
 * that do not implement specific logic.
 * <p>
 * All implementations should extend this class for forward compatibility.
 */
public abstract class MatchValueBase<Value> 
                extends SelfDescribingBase 
                implements MatchValue<Value> {
    
    public MatchValueBase() {
    }

    /**
     * Calls {@link #matches(org.hamcrest.Matcher)} with an
     * {@link ElementMatcherWrapper}.
     */
    @Override
    public boolean matches(Matcher<? super Value> matcher) {
        return matches(new ElementMatcherWrapper<>(-1, matcher));
    }

    /**
     * Calls {@link #matches(org.hamcrest.Matcher)} and returns the result of
     * {@link #matchResult()}.
     */
    @Override
    public MatchResult<?> matchResult(Matcher<? super Value> matcher) {
        matches(matcher);
        return matchResult();
    }

    /**
     * Calls {@link #matches(ElementMatcher)} and returns the result of
     * {@link #matchResult()}.
     */
    @Override
    public MatchResult<?> matchResult(ElementMatcher<? super Value> matcher) {
        matches(matcher);
        return matchResult();
    }

    /**
     * Calls {@link MatchValueAdapter#adapt(MatchValue) adapter.adapt(this)}.
     */
    @Override
    public <Property> MatchValue<Property> get(MatchValueAdapter<? super Value, Property> adapter) {
        return adapter.adapt(this);
    }

    /**
     * Calls {@link #describeValue(org.hamcrest.Description)}
     */
    @Override
    public void describeTo(Description description) {
        describeValue(description);
    }
}
