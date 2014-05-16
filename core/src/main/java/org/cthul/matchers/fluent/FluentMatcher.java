package org.cthul.matchers.fluent;

import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * A fluent that builds a matcher.
 * The matchers can be combined with one of {@link #and() and()}(default), 
 * {@link #or() or()}, or {@link #xor() xor()}. Attempting to change the 
 * chain type once it is set causes an {@link IllegalStateException}.
 * <p>
 * Once the fluent was used as a matcher, or {@link #getMatcher() getMatcher()}
 * was called, it will be frozen. 
 * Any further modification causes an {@link IllegalStateException}.
 * @param <Value> value type of the fluent
 * @param <Match> type of the matcher that is built
 */
public interface FluentMatcher<Value, Match> 
                extends Fluent<Value>, 
                        FluentPropertyMatcher<Value, Value, Match>,
                        QuickDiagnosingMatcher<Match> {
    
    /**
     * Returns the matcher that was build by this fluent.
     * @return matcher
     */
    QuickDiagnosingMatcher<Match> getMatcher();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> is();
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> has();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> not();
    
    /**
     * Defines this fluent to build a conjunction of matchers.
     * @return this
     * @throws IllegalStateException if {@link #or() or()} or 
     *          {@link #xor() xor()} was called before
     */
    FluentMatcher<Value, Match> and();

    /**
     * Defines this fluent to build a disjunction of matchers.
     * @return this
     * @throws IllegalStateException if {@link #and() and()} or 
     *          {@link #xor() xor()} was called before
     */
    FluentMatcher<Value, Match> or();

    /**
     * Defines this fluent to build an exclusive disjunction of matchers.
     * @return this
     * @throws IllegalStateException if {@link #and() and()} or 
     *          {@link #or() or()} was called before
     */
    FluentMatcher<Value, Match> xor();

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if the fluent was frozen by using it as 
     *          a matcher or calling {@link #getMatcher() getMatcher()}
     */
    @Override
    FluentMatcher<Value, Match> __(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> is(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> has(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> not(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> isNot(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> hasNot(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> equalTo(Value value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> is(Value value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> not(Value value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> isNot(Value value);

    /**
     * Equivalent to {@link #and() and()}{@link #__(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentMatcher<Value, Match> and(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #or() or()}{@link #__(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentMatcher<Value, Match> or(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #xor() xor()}{@link #__(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentMatcher<Value, Match> xor(Matcher<? super Value> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> all(Matcher<? super Value>... matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> any(Matcher<? super Value>... matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> none(Matcher<? super Value>... matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> matches(int count, Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> matches(Matcher<? super Integer> countMatcher, Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> matches(ChainFactory chainType, Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> __(MatchValueAdapter<? super Value, P> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> has(MatchValueAdapter<? super Value, P> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> not(MatchValueAdapter<? super Value, P> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> hasNot(MatchValueAdapter<? super Value, P> adapter);

    /**
     * Equivalent to {@link #and() and()}{@link #__(MatchValueAdapter) \u2024_(adapter)}.
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentPropertyMatcher<Value, P, Match> and(MatchValueAdapter<? super Value, P> adapter);

    /**
     * Equivalent to {@link #or() or()}{@link #__(MatchValueAdapter) \u2024_(adapter)}.
     * @param <P> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentPropertyMatcher<Value, P, Match> or(MatchValueAdapter<? super Value, P> adapter);

    /**
     * Equivalent to {@link #xor() xor()}{@link #__(MatchValueAdapter) \u2024_(adapter)}.
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentPropertyMatcher<Value, P, Match> xor(MatchValueAdapter<? super Value, P> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentMatcher<Value, Match> __(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentMatcher<Value, Match> has(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher);

    /**
     * Equivalent to {@link #and() and()}{@link #__(MatchValueAdapter, Matcher) \u2024_(adapter, matcher)}.
     * @param <P> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return this
     */
    <P> FluentMatcher<Value, Match> and(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher);
    
    /**
     * Equivalent to {@link #or() or()}{@link #__(MatchValueAdapter, Matcher) \u2024_(adapter, matcher)}.
     * @param <P> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return this
     */
    <P> FluentMatcher<Value, Match> or(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher);
    
    /**
     * Equivalent to {@link #xor() xor()}{@link #__(MatchValueAdapter, Matcher) \u2024_(adapter, matcher)}.
     * @param <P> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return this
     */
    <P> FluentMatcher<Value, Match> xor(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyMatcher.Both<Value, Value, Match> both(Matcher<? super Value> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyMatcher.Either<Value, Value, Match> either(Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyMatcher.Neither<Value, Value, Match> neither(Matcher<? super Value>... matchers);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyMatcher.MatchesSome<Value, Value, Match> matches(int count);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyMatcher.MatchesSome<Value, Value, Match> matches(Matcher<? super Integer> countMatcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyMatcher.MatchesSome<Value, Value, Match> matches(ChainFactory chainType);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <Property extends Value> FluentMatcher<Value, Match> isA(Class<Property> clazz, Matcher<? super Property> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    <Property extends Value> FluentPropertyMatcher.IsA<Value, Property, Match> isA(Class<Property> clazz);
}
