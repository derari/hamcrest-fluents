package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent matcher that matches a property of a value. After the match, the chain
 * goes back to the value itself.
 *
 * @param <Value> type of the actual value
 * @param <Property> type of the property to be matched
 * @param <Match> type of the matcher that is built
 */
public interface FluentPropertyMatcher<Value, Property, Match> 
                extends FluentProperty<Value, Property> {

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyMatcher<Value, Property, Match> is();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyMatcher<Value, Property, Match> has();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyMatcher<Value, Property, Match> not();
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> _(Matcher<? super Property> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> is(Matcher<? super Property> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> has(Matcher<? super Property> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> not(Matcher<? super Property> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> isNot(Matcher<? super Property> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> hasNot(Matcher<? super Property> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> all(Matcher<? super Property>... matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> any(Matcher<? super Property>... matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentMatcher<Value, Match> none(Matcher<? super Property>... matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> _(MatchValueAdapter<? super Property, P> adapter);
        
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> has(MatchValueAdapter<? super Property, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> not(MatchValueAdapter<? super Property, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> hasNot(MatchValueAdapter<? super Property, P> adapter);
    
    /**
     * {@inheritDoc}
     */
    @Override
    Both<Value, Property, Match> both(Matcher<? super Property> matcher);
    
    /**
     * Builds the conjunction of matchers and applies them against the fluent
     * it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @param <Match> type of the matcher that is built
     * @see #both(org.hamcrest.Matcher) 
     */
    interface Both<Value, Property, Match> extends FluentProperty.Both<Value, Property> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        FluentMatcher<Value, Match> and(Matcher<? super Property> matcher);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    Either<Value, Property, Match> either(Matcher<? super Property>... matchers);
    
    /**
     * Builds the (exclusive) disjunction of matchers and applies them 
     * against the fluent it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @param <Match> type of the matcher that is built
     * @see #either(org.hamcrest.Matcher[]) 
     */
    interface Either<Value, Property, Match> extends FluentProperty.Either<Value, Property> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        FluentMatcher<Value, Match> or(Matcher<? super Property> matcher);
        
        /**
         * {@inheritDoc}
         */
        @Override
        FluentMatcher<Value, Match> xor(Matcher<? super Property> matcher);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    Neither<Value, Property, Match> neither(Matcher<? super Property>... matchers);
    
    /**
     * Builds the joint denial of matchers and applies them against the fluent 
     * it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @param <Match> type of the matcher that is built
     * @see #neither(org.hamcrest.Matcher[]) 
     */
    interface Neither<Value, Property, Match> extends FluentProperty.Neither<Value, Property> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        FluentMatcher<Value, Match> nor(Matcher<? super Property> matcher);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    <Property2 extends Property> FluentMatcher<? extends Value, Match> isA(Class<Property2> clazz, Matcher<? super Property2> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <Property2 extends Property> IsA<? extends Value, Property2, Match> isA(Class<Property2> clazz);
    
    /**
     * A proxy for a {@link FluentMatcher} that allows to also match against some
     * previously defined property via {@link IsA#that() that()}.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @param <Match> type of the matcher that is built
     * @see #isA(java.lang.Class) 
     */
    interface IsA<Value, Property, Match> 
                    extends FluentProperty.IsA<Value, Property>,
                            FluentMatcher<Value, Match> {

        /**
         * {@inheritDoc}
         */
        @Override
        FluentPropertyMatcher<Value, Property, Match> that();
        
        /**
         * {@inheritDoc}
         */
        @Override
        FluentPropertyMatcher<Value, Property, Match> thatIs();

        /**
         * {@inheritDoc}
         */
        @Override
        FluentMatcher<Value, Match> that(Matcher<? super Property> matcher);

        /**
         * {@inheritDoc}
         */
        @Override
        FluentMatcher<Value, Match> thatIs(Matcher<? super Property> matcher);
    }
}