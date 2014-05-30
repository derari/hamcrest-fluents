package org.cthul.matchers.fluent;

import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent that matches a property of a value. 
 * <p>
 * After the match, the fluent chain goes back to the value itself.
 * @param <Value> type of the actual value
 * @param <Property> type of the property to be matched
 */
public interface FluentProperty<Value, Property> {

    /**
     * Prepends "is" to the next matcher's description.
     * @return this
     */
    FluentProperty<Value, Property> is();

    /**
     * Prepends "has" to the next matcher's description.
     * @return this
     */
    FluentProperty<Value, Property> has();

    /**
     * Negates the next matcher's match result and 
     * prepends "not" to its description.
     * @return this
     */
    FluentProperty<Value, Property> not();

    /**
     * Adds a matcher to the fluent.
     * Actual behavior is implementation specific.
     * <p>
     * After the call, the fluent chain goes back to the value itself.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> __(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #is() is()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> is(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> has(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #not() not()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> not(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #is() is()}{@link #not() .not()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> isNot(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> hasNot(Matcher<? super Property> matcher);
    
    /**
     * Adds a matcher that checks if the property is 
     * {@link org.hamcrest.core.IsEqual#equalTo(Object) equalTo}
     * the given value.
     * @param value the value
     * @return fluent
     * @see org.hamcrest.core.IsEqual#equalTo(Object) 
     */
    Fluent<Value> equalTo(Property value);
    
    /**
     * Equivalent to {@link #is() is()}{@link #equalTo(Object) .equalTo(value)}. 
     * @param value the value
     * @return fluent
     */
    Fluent<Value> is(Property value);
    
    /**
     * Equivalent to {@link #not() not()}{@link #equalTo(Object) .equalTo(value)}.
     * @param value the value
     * @return fluent
     */
    Fluent<Value> not(Property value);
    
    /**
     * Equivalent to {@link #is() is()}{@link #not() .not()}{@link #equalTo(Object) .equalTo(value)}.
     * @param value the value
     * @return fluent
     */
    Fluent<Value> isNot(Property value);

    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the conjunction of {@code matchers}.
     * @param matchers the matchers
     * @return fluent
     */
    Fluent<Value> all(Matcher<? super Property>... matchers);

    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the disjunction of {@code matchers}.
     * @param matchers the matchers
     * @return fluent
     */
    Fluent<Value> any(Matcher<? super Property>... matchers);

    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the joint denial of {@code matchers}.
     * @param matchers the matchers
     * @return fluent
     */
    Fluent<Value> none(Matcher<? super Property>... matchers);
    
    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with a matcher that succeeds when exactly {@code count} of 
     * {@code matchers} succeed.
     * @param count
     * @param matchers
     * @return fluent
     */
    Fluent<Value> matches(int count, Matcher<? super Property>... matchers);
    
    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with a matcher that succeeds when the number of succeeding
     * {@code matchers} is matched by {@code countMatcher}.
     * @param countMatcher
     * @param matchers
     * @return fluent
     */
    Fluent<Value> matches(Matcher<? super Integer> countMatcher, Matcher<? super Property>... matchers);
    
    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the result of {@code chainType.create(matchers)}.
     * @param chainType
     * @param matchers
     * @return fluent
     */
    Fluent<Value> matches(ChainFactory chainType, Matcher<? super Property>... matchers);

    /**
     * Returns a {@link FluentProperty} that uses the {@code adapter} to
     * apply matchers against a property of the actual value.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentProperty<Value, NextProperty> __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #has() has()}{@link #__(MatchValueAdapter) .__(adapter)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentProperty<Value, NextProperty> has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #not() not()}{@link #__(MatchValueAdapter) .__(adapter)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentProperty<Value, NextProperty> not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}{@link #__(MatchValueAdapter) .__(adapter)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentProperty<Value, NextProperty> hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #__(Matcher) __(adapter.adapt(matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return fluent
     */
    <NextProperty> Fluent<Value> __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #__(Matcher) .__(adapter.adapt(matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return property fluent
     */
    <NextProperty> Fluent<Value> has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #__(Matcher) .__(adapter.adapt(matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return property fluent
     */
    <NextProperty> Fluent<Value> not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}{@link #__(Matcher) .__(adapter.adapt(matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return property fluent
     */
    <NextProperty> Fluent<Value> hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * Returns a {@link Both} that applies the conjunction of 
     * the given and another matcher to the current fluent.
     * <p>
     * After the match, the fluent chain goes back to the actual value.
     * @param matcher the matcher
     * @return both fluent
     */
    Both<Value, Property> both(Matcher<? super Property> matcher);

    /**
     * Builds the conjunction of matchers and applies them against the fluent
     * it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #both(org.hamcrest.Matcher) 
     */
    interface Both<Value, Property> {

        /**
         * Builds and applies the conjunction of the matchers.
         * @param matcher the matcher
         * @return fluent
         * @see #both(org.hamcrest.Matcher) 
         */
        Fluent<Value> and(Matcher<? super Property> matcher);
    }

    /**
     * Returns a {@link Either} that applies the (exclusive) disjunction of 
     * the given and another matcher to the current fluent.
     * <p>
     * After the match, the fluent chain goes back to the actual value.
     * @param matchers the matchers
     * @return either fluent
     */
    Either<Value, Property> either(Matcher<? super Property>... matchers);

    /**
     * Builds the (exclusive) disjunction of matchers and applies them 
     * against the fluent it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #either(org.hamcrest.Matcher[]) 
     */
    interface Either<Value, Property> {

        /**
         * Builds and applies the disjunction of the matchers.
         * @param matcher the matcher
         * @return fluent
         * @see #either(org.hamcrest.Matcher[])
         */
        Fluent<Value> or(Matcher<? super Property> matcher);

        /**
         * Builds and applies the exclusive disjunction of the matchers.
         * @param matcher the matcher
         * @return fluent
         * @see #either(org.hamcrest.Matcher[])
         */
        Fluent<Value> xor(Matcher<? super Property> matcher);
    }

    /**
     * Returns a {@link Neither} that applies the joint denial of 
     * the given and another matcher to the current fluent.
     * <p>
     * After the match, the fluent chain goes back to the actual value.
     * @param matchers the matchers
     * @return neither fluent
     */
    Neither<Value, Property> neither(Matcher<? super Property>... matchers);

    /**
     * Builds the joint denial of matchers and applies them against the fluent 
     * it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #neither(org.hamcrest.Matcher[]) 
     */
    interface Neither<Value, Property> {

        /**
         * Builds and applies the joint denial of the matchers.
         * @param matcher the matcher
         * @return fluent
         * @see #either(org.hamcrest.Matcher[])
         */
        Fluent<Value> nor(Matcher<? super Property> matcher);
    }
    
    /**
     * Returns a {@link MatchesSome} that expects 
     * {@code count} matchers to succeed.
     * @param count
     * @return matches-some fluent
     */
    MatchesSome<Value, Property> matches(int count);
    
    /**
     * Returns a {@link MatchesSome} that expects {@code countMatcher} 
     * to accept the number of successful matchers.
     * @param countMatcher
     * @return matches-some fluent
     */
    MatchesSome<Value, Property> matches(Matcher<? super Integer> countMatcher);
    
    /**
     * Returns a {@link MatchesSome} that combines and applies matchers
     * using the {@code chainType}.
     * @param chainType
     * @return matches-some
     */
    MatchesSome<Value, Property> matches(ChainFactory chainType);
    
    /**
     * Combines matchers with a chain factory and applies them against 
     * the fluent it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #matches(int) 
     * @see #matches(org.hamcrest.Matcher) 
     * @see #matches(org.cthul.matchers.chain.ChainFactory) 
     */
    interface MatchesSome<Value, Property> {
        
        /**
         * Builds and applies the combined matchers.
         * @param matchers
         * @return fluent
         * @see #matches(int) 
         * @see #matches(org.hamcrest.Matcher)  
         * @see #matches(org.cthul.matchers.chain.ChainFactory) 
         */
        Fluent<Value> of(Matcher<? super Property>... matchers);
    }

    /**
     * Adds a matcher to the fluent that matches only instances of {@code clazz}
     * that are also matched by {@code matcher}.
     * @param <Property2> expected type
     * @param clazz expected type
     * @param matcher the matcher
     * @return fluent
     */
    <Property2 extends Property> Fluent<? extends Value> isA(Class<Property2> clazz, Matcher<? super Property2> matcher);

    /**
     * Immediately adds a matcher to the fluent that matches only 
     * instances of {@code clazz}.
     * <p>
     * Returns a {@link IsA} that serves as a proxy for the fluent of the 
     * actual value, but also allows to further match against the property,
     * using the new type, via {@link IsA#that() that()}.
     * @param <Property2> expected type
     * @param clazz expected type
     * @return isA fluent
     */
    <Property2 extends Property> IsA<? extends Value, Property2> isA(Class<Property2> clazz);

    /**
     * A proxy for a {@link Fluent} that allows to also match against some
     * previously defined property via {@link IsA#that() that()}.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #isA(java.lang.Class) 
     */
    interface IsA<Value, Property> extends Fluent<Value> {

        /**
         * Returns a {@link FluentProperty} that matches against the
         * previously defined property.
         * @return fluent property
         * @see #thatIs() 
         * @see #that(org.hamcrest.Matcher) 
         * @see #thatIs(org.hamcrest.Matcher)
         */
        FluentProperty<Value, Property> that();

        /**
         * Equivalent to {@link #that() that()}{@link FluentProperty#is() .is()}.
         * @return fluent property
         */
        FluentProperty<Value, Property> thatIs();

        /**
         * Equivalent to {@link #that() that()}{@link FluentProperty#__(Matcher) .__(matcher)}.
         * @param matcher the matcher
         * @return fluent 
         */
        Fluent<Value> that(Matcher<? super Property> matcher);

        /**
         * Equivalent to {@link #that() that()}{@link FluentProperty#is() .is()}{@link FluentProperty#__(Matcher) .__(matcher)}.
         * @param matcher the matcher
         * @return fluent 
         */
        Fluent<Value> thatIs(Matcher<? super Property> matcher);
    }
}
