package org.cthul.matchers.fluent;

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
    Fluent<Value> _(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #is() is()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> is(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> has(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #not() not()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> not(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #is() is()}{@link #not() \u2024not()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> isNot(Matcher<? super Property> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #not() \u2024not()}{@link #_(Matcher) \u2024_(matcher)}.
     * @param matcher the matcher
     * @return fluent
     */
    Fluent<Value> hasNot(Matcher<? super Property> matcher);
    
    /**
     * Adds a matcher that checks if the property is {@code equalTo}
     * the given value.
     * @param value the value
     * @return fluent
     */
    Fluent<Value> _(Property value);
    
    /**
     * Equivalent to {@link #is() is()}{@link #_(Object) \u2024_(value)}. 
     * @param value the value
     * @return fluent
     */
    Fluent<Value> is(Property value);
    
    /**
     * Equivalent to {@link #not() not()}{@link #_(Object) \u2024_(value)}.
     * @param value the value
     * @return fluent
     */
    Fluent<Value> not(Property value);
    
    /**
     * Equivalent to {@link #is() is()}{@link #not() \u2024not()}{@link #_(Object) \u2024_(value)}.
     * @param value the value
     * @return fluent
     */
    Fluent<Value> isNot(Property value);

    /**
     * Equivalent to calling {@link #_(org.hamcrest.Matcher) _(matcher)}
     * with the conjunction of {@code matchers}.
     * @param matchers the matchers
     * @return fluent
     */
    Fluent<Value> all(Matcher<? super Property>... matchers);

    /**
     * Equivalent to calling {@link #_(org.hamcrest.Matcher) _(matcher)}
     * with the disjunction of {@code matchers}.
     * @param matchers the matchers
     * @return fluent
     */
    Fluent<Value> any(Matcher<? super Property>... matchers);

    /**
     * Equivalent to calling {@link #_(org.hamcrest.Matcher) _(matcher)}
     * with the joint denial of {@code matchers}.
     * @param matchers the matchers
     * @return fluent
     */
    Fluent<Value> none(Matcher<? super Property>... matchers);

    /**
     * Returns a {@link FluentProperty} that uses the {@code adapter} to
     * apply matchers against a property of the actual value.
     * @param <P> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentProperty<Value, P> _(MatchValueAdapter<? super Property, P> adapter);

    /**
     * Equivalent to {@link #has() has()}{@link #_(MatchValueAdapter) \u2024_(adapter)}.
     * @param <P> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentProperty<Value, P> has(MatchValueAdapter<? super Property, P> adapter);

    /**
     * Equivalent to {@link #not() not()}{@link #_(MatchValueAdapter) \u2024_(adapter)}.
     * @param <P> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentProperty<Value, P> not(MatchValueAdapter<? super Property, P> adapter);

    /**
     * Equivalent to {@link #has() has()}{@link #not() \u2024not()}{@link #_(MatchValueAdapter) \u2024_(adapter)}.
     * @param <P> property type
     * @param adapter the adapter
     * @return property fluent
     */
    <P> FluentProperty<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter);

    /**
     * Equivalent to {@link #_(Matcher) _(adapter.adapt(matcher)}.
     * @param <P> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return fluent
     */
    <P> Fluent<Value> _(MatchValueAdapter<? super Property, P> adapter, Matcher<P> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #_(Matcher) \u2024_(adapter.adapt(matcher)}.
     * @param <P> property type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return property fluent
     */
    <P> Fluent<Value> has(MatchValueAdapter<? super Property, P> adapter, Matcher<P> matcher);

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
         * Equivalent to {@link #that() that()}{@link FluentProperty#is() \u2024is()}.
         * @return fluent property
         */
        FluentProperty<Value, Property> thatIs();

        /**
         * Equivalent to {@link #that() that()}{@link FluentProperty#_(Matcher) \u2024_(matcher)}.
         * @param matcher the matcher
         * @return fluent 
         */
        Fluent<Value> that(Matcher<? super Property> matcher);

        /**
         * Equivalent to {@link #that() that()}{@link FluentProperty#is() \u2024is()}{@link FluentProperty#_(Matcher) \u2024_(matcher)}.
         * @param matcher the matcher
         * @return fluent 
         */
        Fluent<Value> thatIs(Matcher<? super Property> matcher);
    }
}
