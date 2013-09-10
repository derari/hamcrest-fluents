package org.cthul.matchers.fluent.value;

import org.cthul.matchers.chain.AndChainMatcher;
import org.cthul.matchers.diagnose.nested.Nested;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class MatchValueBase<Value> 
                extends Nested.Result<MatchValue<Value>, Matcher<?>>
                implements MatchValue<Value>, MatchValue.Mismatch<Value> {
    
    private final AndChainMatcher.Builder<Element<Value>> allMatchers = new AndChainMatcher.Builder<>();

    public MatchValueBase() {
        super(null, null);
    }

    @Override
    public boolean matches(ElementMatcher<Value> matcher) {
        allMatchers.and(matcher);
        return true;
    }

    @Override
    public MatchValue<Value> matchResult(ElementMatcher<Value> matcher) {
        matches(matcher);
        return this;
    }
    
    protected Matcher<?> tmpMatcher() {
        return allMatchers;
    }

    @Override
    public MatchValue<Value> getValue() {
        return this;
    }

    @Override
    public Matcher<?> getMatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MatchValue.Mismatch<Value> getMismatch() {
        return matched() ? null : this;
    }

    @Override
    public void describeExpected(Description d) {
        Expectation e = new Expectation();
        describeExpected(e);
        d.appendDescriptionOf(e);
    }

    @Override
    public <Property> MatchValue<Property> get(MatchValueAdapter<? super Value, Property> adapter) {
        return adapter.adapt(this);
    }

}
