package org.cthul.matchers.fluent;

import org.cthul.matchers.chain.ChainFactory;
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
    Fluent<Value> __(Matcher<? super Value> matcher);

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
    Fluent<Value> equalTo(Value value);
    
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
     * @return this
     */
    @Override
    Fluent<Value> matches(int count, Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> matches(Matcher<? super Integer> countMatcher, Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    Fluent<Value> matches(ChainFactory chainType, Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentProperty<Value, NextProperty> __(MatchValueAdapter<? super Value, ? extends NextProperty> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentProperty<Value, NextProperty> has(MatchValueAdapter<? super Value, ? extends NextProperty> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentProperty<Value, NextProperty> not(MatchValueAdapter<? super Value, ? extends NextProperty> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentProperty<Value, NextProperty> hasNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    <NextProperty> Fluent<Value> __(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    <NextProperty> Fluent<Value> has(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    <NextProperty> Fluent<Value> not(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);
    
    /**
     * {@inheritDoc}
     * @return this
     */
    @Override
    <NextProperty> Fluent<Value> hasNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    Both<Value, Value> both(Matcher<? super Value> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    Either<Value, Value> either(Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    MatchesSome<Value, Value> matches(int count);
    
    /**
     * {@inheritDoc}
     */
    @Override
    MatchesSome<Value, Value> matches(Matcher<? super Integer> countMatcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    MatchesSome<Value, Value> matches(ChainFactory chainType);
    
    /**
     * {@inheritDoc}
     * @param <Value2> expected type
     */
    @Override
    <Value2 extends Value> Fluent<? extends Value> isA(Class<Value2> clazz, Matcher<? super Value2> matcher);
        
    /**
     * {@inheritDoc}
     */
    @Override
    <Property2 extends Value> FluentProperty.IsA<? extends Value, Property2> isA(Class<Property2> clazz);
}
