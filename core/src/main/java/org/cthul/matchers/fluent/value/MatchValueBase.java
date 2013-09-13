package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.SelfDescribingBase;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class MatchValueBase<Value> 
                extends SelfDescribingBase 
                implements MatchValue<Value> {
    
    public MatchValueBase() {
    }

    @Override
    public boolean matches(Matcher<? super Value> matcher) {
        // todo: create element matcher
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MatchResult<?> matchResult(Matcher<? super Value> matcher) {
        matches(matcher);
        return matchResult();
    }

    @Override
    public MatchResult<?> matchResult(ElementMatcher<? super Value> matcher) {
        matches(matcher);
        return matchResult();
    }

    @Override
    public <Property> MatchValue<Property> get(MatchValueAdapter<? super Value, Property> adapter) {
        return adapter.adapt(this);
    }

    @Override
    public void describeTo(Description description) {
        describeValue(description);
    }
}
