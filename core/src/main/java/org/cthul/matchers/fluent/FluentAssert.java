package org.cthul.matchers.fluent;

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
     * Equivalent to {@link #and() and()}{@link #is() \u2024is()}.
     * @return this
     */
    FluentAssert<Value> andIs();

    /**
     * Equivalent to {@link #and() and()}{@link #not() \u2024not()}.
     * @return this
     */
    FluentAssert<Value> andNot();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> _(Matcher<? super Value> matcher);

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
     * Equivalent to {@link #and() and()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentAssert<Value> and(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #and() and()}{@link #is() \u2024is()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentAssert<Value> andIs(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #and() and()}{@link #not() \u2024not()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentAssert<Value> andNot(Matcher<? super Value> matcher);

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
    <P> FluentPropertyAssert<Value, P> _(MatchValueAdapter<? super Value, P> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyAssert<Value, P> has(MatchValueAdapter<? super Value, P> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyAssert<Value, P> not(MatchValueAdapter<? super Value, P> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyAssert<Value, P> hasNot(MatchValueAdapter<? super Value, P> adapter);

    /**
     * Equivalent to {@link #and() and()}{@link #_(MatchValueAdapter) \u2024_(adapter)}.
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentPropertyAssert<Value, P> and(MatchValueAdapter<? super Value, P> adapter);

    /**
     * Equivalent to {@link #and() and()}{@link #not() \u2024not()}{@link #_(MatchValueAdapter) \u2024_(adapter)}.
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentPropertyAssert<Value, P> andNot(MatchValueAdapter<? super Value, P> adapter);

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
    <Value2 extends Value> FluentAssert<Value2> isA(Class<Value2> clazz, Matcher<? super Value2> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    <Value2 extends Value> FluentPropertyAssert.IsA<Value2, Value2> isA(Class<Value2> clazz);
}
