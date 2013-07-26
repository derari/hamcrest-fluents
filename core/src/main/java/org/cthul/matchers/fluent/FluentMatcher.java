package org.cthul.matchers.fluent;

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
     * Equivalent to {@link #and() and()}{@link #not() \u2024not()}.
     * @return this
     */
    FluentMatcher<Value, Match> andNot();
    
    /**
     * Defines this fluent to build a disjunction of matchers.
     * @return this
     * @throws IllegalStateException if {@link #and() and()} or 
     *          {@link #xor() xor()} was called before
     */
    FluentMatcher<Value, Match> or();

    /**
     * Equivalent to {@link #or() or()}{@link #not() \u2024not()}.
     * @return this
     */
    FluentMatcher<Value, Match> orNot();
    
    /**
     * Defines this fluent to build an exclusive disjunction of matchers.
     * @return this
     * @throws IllegalStateException if {@link #and() and()} or 
     *          {@link #or() or()} was called before
     */
    FluentMatcher<Value, Match> xor();

    /**
     * Equivalent to {@link #xor() xor()}{@link #not() \u2024not()}.
     * @return this
     */
    FluentMatcher<Value, Match> xorNot();
    
    /**
     * {@inheritDoc}
     * @throws IllegalStateException if the fluent was frozen by using it as 
     *          a matcher or calling {@link #getMatcher() getMatcher()}
     */
    @Override
    FluentMatcher<Value, Match> _(Matcher<? super Value> matcher);

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
     * Equivalent to {@link #and() and()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentMatcher<Value, Match> and(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #and() and()}{@link #not() \u2024not()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentMatcher<Value, Match> andNot(Matcher<? super Value> matcher);
    
    /**
     * Equivalent to {@link #or() or()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentMatcher<Value, Match> or(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #or() or()}{@link #not() \u2024not()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentMatcher<Value, Match> orNot(Matcher<? super Value> matcher);
    
    /**
     * Equivalent to {@link #xor() xor()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentMatcher<Value, Match> xor(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #xor() xor()}{@link #not() \u2024not()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return this
     */
    FluentMatcher<Value, Match> xorNot(Matcher<? super Value> matcher);
    
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
    <P> FluentPropertyMatcher<Value, P, Match> _(MatchValueAdapter<? super Value, P> matcher);

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
     * Equivalent to {@link #and() and()}{@link #_(MatchValueAdapter) \u2024_(adapter)}.
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentPropertyMatcher<Value, P, Match> and(MatchValueAdapter<? super Value, P> adapter);

    /**
     * Equivalent to {@link #and() and()}{@link #not() \u2024not()}{@link #_(MatchValueAdapter) \u2024_(adapter)}.
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentPropertyMatcher<Value, P, Match> andNot(MatchValueAdapter<? super Value, P> adapter);

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
    <Value2 extends Value> FluentMatcher<Value2, Match> isA(Class<Value2> clazz, Matcher<? super Value2> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <Value2 extends Value> FluentPropertyMatcher.IsA<Value2, Value2, Match> isA(Class<Value2> clazz);
}
