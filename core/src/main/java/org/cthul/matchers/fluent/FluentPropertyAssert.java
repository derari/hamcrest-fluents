package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent assert that matches a property of a value. After the match, the chain
 * goes back to the value itself.
 *
 * @param <Value> type of the actual value
 * @param <Property> type of the property to be matched
 */
public interface FluentPropertyAssert<Value, Property> 
                extends FluentProperty<Value, Property> {

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyAssert<Value, Property> is();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyAssert<Value, Property> has();

    /**
     * {@inheritDoc}
     */
    @Override
    FluentPropertyAssert<Value, Property> not();

    /**
     * Immediately matches the given value against {@code matcher}.
     * <p>
     * After the call, the fluent chain goes back to the value itself.
     * @param matcher the matcher
     * @return fluent
     */
    @Override
    FluentAssert<Value> _(Matcher<? super Property> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> is(Matcher<? super Property> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> has(Matcher<? super Property> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> not(Matcher<? super Property> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> isNot(Matcher<? super Property> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> hasNot(Matcher<? super Property> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> _(Property value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> is(Property value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> not(Property value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> isNot(Property value);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> all(Matcher<? super Property>... matchers);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> any(Matcher<? super Property>... matchers);

    /**
     * {@inheritDoc}
     */
    @Override
    FluentAssert<Value> none(Matcher<? super Property>... matchers);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyAssert<Value, P> _(MatchValueAdapter<? super Property, P> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyAssert<Value, P> has(MatchValueAdapter<? super Property, P> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyAssert<Value, P> not(MatchValueAdapter<? super Property, P> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentPropertyAssert<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter);

    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentAssert<Value> _(MatchValueAdapter<? super Property, P> adapter, Matcher<P> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    <P> FluentAssert<Value> has(MatchValueAdapter<? super Property, P> adapter, Matcher<P> matcher);
    
    /**
     * {@inheritDoc}
     */
    @Override
    Both<Value, Property> both(Matcher<? super Property> matcher);

    /**
     * Builds the conjunction of matchers and applies them against the fluent
     * it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #both(org.hamcrest.Matcher) 
     */
    interface Both<Value, Property> extends FluentProperty.Both<Value, Property> {

        /**
         * {@inheritDoc}
         */
        @Override
        FluentAssert<Value> and(Matcher<? super Property> matcher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Either<Value, Property> either(Matcher<? super Property>... matchers);

    /**
     * Builds the (exclusive) disjunction of matchers and applies them 
     * against the fluent it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #either(org.hamcrest.Matcher[]) 
     */
    interface Either<Value, Property> extends FluentProperty.Either<Value, Property> {

        /**
         * {@inheritDoc}
         */
        @Override
        FluentAssert<Value> or(Matcher<? super Property> matcher);

        /**
         * {@inheritDoc}
         */
        @Override
        FluentAssert<Value> xor(Matcher<? super Property> matcher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Neither<Value, Property> neither(Matcher<? super Property>... matchers);

    /**
     * Builds the joint denial of matchers and applies them against the fluent 
     * it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #neither(org.hamcrest.Matcher[]) 
     */
    interface Neither<Value, Property> extends FluentProperty.Neither<Value, Property> {
        
        /**
         * {@inheritDoc}
         */
        @Override
        FluentAssert<Value> nor(Matcher<? super Property> matcher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    <Property2 extends Property> FluentAssert<? extends Value> isA(Class<Property2> clazz, Matcher<? super Property2> matcher);

    /**
     * {@inheritDoc}
     */
    @Override
    <Property2 extends Property> IsA<? extends Value, Property2> isA(Class<Property2> clazz);

    /**
     * A proxy for a {@link FluentAssert} that allows to also match against some
     * previously defined property via {@link IsA#that() that()}.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #isA(java.lang.Class) 
     */
    interface IsA<Value, Property>
                    extends FluentProperty.IsA<Value, Property>,
                            FluentAssert<Value> {

        /**
         * {@inheritDoc}
         */
        @Override
        FluentPropertyAssert<Value, Property> that();

        /**
         * {@inheritDoc}
         */
        @Override
        FluentPropertyAssert<Value, Property> thatIs();

        /**
         * {@inheritDoc}
         */
        @Override
        FluentAssert<Value> that(Matcher<? super Property> matcher);

        /**
         * {@inheritDoc}
         */
        @Override
        FluentAssert<Value> thatIs(Matcher<? super Property> matcher);
    }
}