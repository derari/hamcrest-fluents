package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Base interface for fluent matcher chains.
 * See {@link FluentMatcher} and {@link FluentAssert} for concrete use-cases.
 * <p>
 * As a {@linkplain FluentProperty property}, it matches against the
 * value itself.
 * 
 * @param <Value> type of value
 */
public interface Fluent<Value> extends FluentProperty<Value, Value> {

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> is();
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> has();

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> not();
    
    /**
     * Adds a matcher to the fluent.
     * Actual behavior is implementation specific.
     * @param matcher
     * @return this
     */
    @Override
    Fluent<Value> _(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> is(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> has(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> not(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> isNot(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> hasNot(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     * @return fluent
     */
    @Override
    Fluent<Value> _(Value value);
    
    /**
     * {@inheritDoc}
     * @return fluent
     */
    @Override
    Fluent<Value> is(Value value);
    
    /**
     * {@inheritDoc}
     * @return fluent
     */
    @Override
    Fluent<Value> not(Value value);
    
    /**
     * {@inheritDoc}
     * @return fluent
     */
    @Override
    Fluent<Value> isNot(Value value);

    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> all(Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> any(Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> none(Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentProperty<Value, P> _(MatchValueAdapter<? super Value, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentProperty<Value, P> has(MatchValueAdapter<? super Value, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentProperty<Value, P> not(MatchValueAdapter<? super Value, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentProperty<Value, P> hasNot(MatchValueAdapter<? super Value, P> adapter);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    <P> Fluent<Value> _(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    <P> Fluent<Value> has(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    Both<Value, Value> both(Matcher<? super Value> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <Property extends Value> Fluent<? extends Value> isA(Class<Property> clazz, Matcher<? super Property> matcher);
        
    /**
     * {@inheritDoc}
     */
    @Override
    <Property extends Value> FluentProperty.IsA<? extends Value, Property> isA(Class<Property> clazz);
}
