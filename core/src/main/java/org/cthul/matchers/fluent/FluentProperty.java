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
public interface FluentProperty<Property, TheFluent> {

    /**
     * Prepends "is" to the next matcher's description.
     * @return this
     */
    FluentProperty<Property, TheFluent> is();

    /**
     * Prepends "has" to the next matcher's description.
     * @return this
     */
    FluentProperty<Property, TheFluent> has();

    /**
     * Negates the next matcher's match result and 
     * prepends "not" to its description.
     * @return this
     */
    FluentProperty<Property, TheFluent> not();

    /**
     * Adds a matcher to the fluent.
     * Actual behavior is implementation specific.
     * <p>
     * After the call, the fluent chain goes back to the value itself.
     * @param matcher the matcher
     * @return fluent
     */
    TheFluent __(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #is() is()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    TheFluent is(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    TheFluent has(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #not() not()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    TheFluent not(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #is() is()}{@link #not() .not()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    TheFluent isNot(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    TheFluent hasNot(Matcher<? super Property> matcher);
    
    /**
     * Adds a matcher that checks if the property is 
     * {@link org.hamcrest.core.IsEqual#equalTo(Object) equalTo}
     * the given value.
     * @param value the value
     * @return fluent
     * @see org.hamcrest.core.IsEqual#equalTo(Object) 
     */
    TheFluent equalTo(Property value);
    
    /**
     * Equivalent to {@link #is() is()}{@link #equalTo(Object) .equalTo(value)}. 
     * @param value the value
     * @return fluent
     */
    TheFluent is(Property value);
    
    /**
     * Equivalent to {@link #not() not()}{@link #equalTo(Object) .equalTo(value)}.
     * @param value the value
     * @return fluent
     */
    TheFluent not(Property value);
    
    /**
     * Equivalent to {@link #is() is()}{@link #not() .not()}{@link #equalTo(Object) .equalTo(value)}.
     * @param value the value
     * @return fluent
     */
    TheFluent isNot(Property value);

    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the conjunction of {@code matchers}.
     * @param matchers the matchers
     * @return fluent
     */
    TheFluent all(Matcher<? super Property>... matchers);

    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the disjunction of {@code matchers}.
     * @param matchers the matchers
     * @return fluent
     */
    TheFluent any(Matcher<? super Property>... matchers);

    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the joint denial of {@code matchers}.
     * @param matchers the matchers
     * @return fluent
     */
    TheFluent none(Matcher<? super Property>... matchers);
    
    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with a matcher that succeeds when exactly {@code count} of 
     * {@code matchers} succeed.
     * @param count
     * @param matchers
     * @return fluent
     */
    TheFluent matches(int count, Matcher<? super Property>... matchers);
    
    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with a matcher that succeeds when the number of succeeding
     * {@code matchers} is matched by {@code countMatcher}.
     * @param countMatcher
     * @param matchers
     * @return fluent
     */
    TheFluent matches(Matcher<? super Integer> countMatcher, Matcher<? super Property>... matchers);
    
    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the result of {@code chainType.create(matchers)}.
     * @param chainType
     * @param matchers
     * @return fluent
     */
    TheFluent matches(ChainFactory chainType, Matcher<? super Property>... matchers);

    /**
     * Returns a {@link FluentProperty} that uses the {@code adapter} to
     * apply matchers against a property of the actual value.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentProperty<NextProperty, ? extends TheFluent> __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #has() has()}{@link #__(MatchValueAdapter) .__(adapter)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentProperty<NextProperty, ? extends TheFluent> has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #not() not()}{@link #__(MatchValueAdapter) .__(adapter)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentProperty<NextProperty, ? extends TheFluent> not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}{@link #__(MatchValueAdapter) .__(adapter)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <NextProperty> FluentProperty<NextProperty, ? extends TheFluent> hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter);

    /**
     * Equivalent to {@link #__(Matcher) __(adapter.adapt(matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return fluent
     */
    <NextProperty> TheFluent __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #__(Matcher) .__(adapter.adapt(matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return property fluent
     */
    <NextProperty> TheFluent has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #__(Matcher) .__(adapter.adapt(matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return property fluent
     */
    <NextProperty> TheFluent not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}{@link #__(Matcher) .__(adapter.adapt(matcher)}.
     * @param <NextProperty> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return property fluent
     */
    <NextProperty> TheFluent hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher);

    /**
     * Adds a matcher to the fluent that matches only instances of {@code clazz}.
     * @param <Property2> expected type
     * @param clazz expected type
     * @return fluent
     */
    <Property2 extends Property> TheFluent isA(Class<Property2> clazz);

    /**
     * Adds a matcher to the fluent that matches only instances of {@code clazz}
     * that are also matched by {@code matcher}.
     * @param <Property2> expected type
     * @param clazz expected type
     * @param matcher the matcher
     * @return fluent
     */
    <Property2 extends Property> TheFluent isA(Class<Property2> clazz, Matcher<? super Property2> matcher);

    /**
     * Immediately adds a matcher to the fluent that matches only 
     * instances of {@code clazz} and changes the type of this fluent property.
     * @param <Property2> expected type
     * @param clazz expected type 
     * @return this
     */
    <Property2 extends Property> FluentProperty<Property2, ? extends TheFluent> as(Class<Property2> clazz);
    
    /**
     * Returns a {@link Both} that applies the conjunction of 
     * the given and another matcher to the current fluent.
     * <p>
     * After the match, the fluent chain goes back to the actual value.
     * @param matcher the matcher
     * @return both fluent
     */
    Both<Property, ? extends TheFluent> both(Matcher<? super Property> matcher);

    /**
     * Builds the conjunction of matchers and applies them against the fluent
     * it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #both(org.hamcrest.Matcher) 
     */
    interface Both<Property, TheFluent> {

        /**
         * Builds and applies the conjunction of the matchers.
         * @param matcher the matcher
         * @return fluent
         * @see #both(org.hamcrest.Matcher) 
         */
        TheFluent and(Matcher<? super Property> matcher);
    }

    /**
     * Returns a {@link Either} that applies the (exclusive) disjunction of 
     * the given and another matcher to the current fluent.
     * <p>
     * After the match, the fluent chain goes back to the actual value.
     * @param matchers the matchers
     * @return either fluent
     */
    Either<Property, ? extends TheFluent> either(Matcher<? super Property>... matchers);

    /**
     * Builds the (exclusive) disjunction of matchers and applies them 
     * against the fluent it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #either(org.hamcrest.Matcher[]) 
     */
    interface Either<Property, TheFluent> {

        /**
         * Builds and applies the disjunction of the matchers.
         * @param matcher the matcher
         * @return fluent
         * @see #either(org.hamcrest.Matcher[])
         */
        TheFluent or(Matcher<? super Property> matcher);

        /**
         * Builds and applies the exclusive disjunction of the matchers.
         * @param matcher the matcher
         * @return fluent
         * @see #either(org.hamcrest.Matcher[])
         */
        TheFluent xor(Matcher<? super Property> matcher);
    }

    /**
     * Returns a {@link Neither} that applies the joint denial of 
     * the given and another matcher to the current fluent.
     * <p>
     * After the match, the fluent chain goes back to the actual value.
     * @param matchers the matchers
     * @return neither fluent
     */
    Neither<Property, ? extends TheFluent> neither(Matcher<? super Property>... matchers);

    /**
     * Builds the joint denial of matchers and applies them against the fluent 
     * it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #neither(org.hamcrest.Matcher[]) 
     */
    interface Neither<Property, TheFluent> {

        /**
         * Builds and applies the joint denial of the matchers.
         * @param matcher the matcher
         * @return fluent
         * @see #either(org.hamcrest.Matcher[])
         */
        TheFluent nor(Matcher<? super Property> matcher);
    }
    
    /**
     * Returns a {@link MatchesSome} that expects 
     * {@code count} matchers to succeed.
     * @param count
     * @return matches-some fluent
     */
    MatchesSome<Property, ? extends TheFluent> matches(int count);
    
    /**
     * Returns a {@link MatchesSome} that expects {@code countMatcher} 
     * to accept the number of successful matchers.
     * @param countMatcher
     * @return matches-some fluent
     */
    MatchesSome<Property, ? extends TheFluent> matches(Matcher<? super Integer> countMatcher);
    
    /**
     * Returns a {@link MatchesSome} that combines and applies matchers
     * using the {@code chainType}.
     * @param chainType
     * @return matches-some
     */
    MatchesSome<Property, ? extends TheFluent> matches(ChainFactory chainType);
    
    /**
     * Combines matchers with a chain factory and applies them against 
     * the fluent it was created from.
     * @param <Value> type of the actual value
     * @param <Property> type of the property to be matched
     * @see #matches(int) 
     * @see #matches(org.hamcrest.Matcher) 
     * @see #matches(org.cthul.matchers.chain.ChainFactory) 
     */
    interface MatchesSome<Property, TheFluent> {
        
        /**
         * Builds and applies the combined matchers.
         * @param matchers
         * @return fluent
         * @see #matches(int) 
         * @see #matches(org.hamcrest.Matcher)  
         * @see #matches(org.cthul.matchers.chain.ChainFactory) 
         */
        TheFluent of(Matcher<? super Property>... matchers);
    }
}
