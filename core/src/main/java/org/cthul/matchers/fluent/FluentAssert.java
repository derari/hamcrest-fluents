package org.cthul.matchers.fluent;

import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * A fluent that evaluates the matchers as they are added.
 * What happens when a match fails is implementation specific, 
 * an exception or error might be thrown.
 * @param <Value> type of the value that is matched
 */
public interface FluentAssert<Value>
                extends Fluent<Value>, 
                        FluentPropertyAssert<Value, Value> {

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> is();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> has();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> not();

    /**
     * Exists only to improve the readability of fluent chains, does nothing.
     * @return this
     */
    FluentAssert<Value> and();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> __(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> is(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> has(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> not(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> isNot(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> hasNot(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #and() and()}{@link #__(Matcher) ._(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentAssert<Value> and(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #and() and()}{@link #not() .not()}{@link #__(Matcher) ._(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentAssert<Value> andNot(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> equalTo(Value value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> is(Value value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> not(Value value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> isNot(Value value);

    /**
     * Equivalent to {@link #and() and()}{@link #equalTo(Object) .equalTo(value)}.
     * @param value the value
     * @return this
     */
    FluentAssert<Value> and(Value value);

    /**
     * Equivalent to {@link #and() and()}{@link #not() .not()}{@link #equalTo(Object) .equalTo(value)}.
     * @param value the value
     * @return this
     */
    FluentAssert<Value> andNot(Value value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> all(Matcher<? super Value>... matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> any(Matcher<? super Value>... matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> none(Matcher<? super Value>... matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> matches(int count, Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> matches(Matcher<? super Integer> countMatcher, Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> matches(ChainFactory chainType, Matcher<? super Value>... matchers);

    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentPropertyAssert<Value, NextProperty> __(MatchValueAdapter<? super Value, ? extends NextProperty> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentPropertyAssert<Value, NextProperty> has(MatchValueAdapter<? super Value, ? extends NextProperty> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentPropertyAssert<Value, NextProperty> not(MatchValueAdapter<? super Value, ? extends NextProperty> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentPropertyAssert<Value, NextProperty> hasNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #and() and()}{@link #__(MatchValueAdapter) ._(adapter)}.
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentPropertyAssert<Value, NextProperty> and(MatchValueAdapter<? super Value, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #and() and()}{@link #not() .not()}{@link #__(MatchValueAdapter) ._(adapter)}.
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentPropertyAssert<Value, NextProperty> andNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentAssert<Value> __(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <NextProperty> FluentAssert<Value> has(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);
    
    /**
     * Equivalent to {@link #and() and()}{@link #__(MatchValueAdapter, Matcher) ._(adapter, matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return this
     */
    <NextProperty> FluentAssert<Value> and(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * Equivalent to {@link #and() and()}{@link #not() .not()}{@link #__(MatchValueAdapter, Matcher) ._(adapter, matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return this
     */
    <NextProperty> FluentAssert<Value> andNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyAssert.Both<Value, Value> both(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyAssert.Either<Value, Value> either(Matcher<? super Value>... matchers);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyAssert.Neither<Value, Value> neither(Matcher<? super Value>... matchers);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyAssert.MatchesSome<Value, Value> matches(int count);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyAssert.MatchesSome<Value, Value> matches(Matcher<? super Integer> countMatcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyAssert.MatchesSome<Value, Value> matches(ChainFactory chainType);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <Value2 extends Value> FluentAssert<Value> isA(Class<Value2> clazz, Matcher<? super Value2> matcher);

    /**
     * Immediately adds a matcher to the fluent that matches only 
     * instances of {@code clazz}.
     * @param <Value2> expected type
     * @param clazz expected type
     * @return isA fluent
     */
    @Override
    <Value2 extends Value> FluentPropertyAssert.IsA<Value, Value2> isA(Class<Value2> clazz);

    @Override
    <Value2 extends Value> FluentAssert<Value2> as(Class<Value2> clazz);
}
