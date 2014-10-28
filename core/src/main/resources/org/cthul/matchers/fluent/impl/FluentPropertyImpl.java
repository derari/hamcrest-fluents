package org.cthul.matchers.fluent.impl;

import org.cthul.matchers.chain.AndChainMatcher;
import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.chain.NOrChainMatcher;
import org.cthul.matchers.chain.OrChainMatcher;
import org.cthul.matchers.chain.SomeOfChainMatcher;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentProperty;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

/**
 *
 * @param <Value>
 * @param <Property>
 * @param <ThisFluent>
 * @param <This>
 */
public interface FluentPropertyImpl
                <Value, Property, ThisFluent extends Fluent<Value>,
                 This extends FluentPropertyImpl<Value, Property, ThisFluent, This>>
                extends FluentProperty<Value, Property> {
//    
//    @Override
//    This is();
//
//    @Override
//    This has();
//
//    @Override
//    This not();
//
//    @Override
//    ThisFluent __(Matcher<? super Property> matcher);
//
//    @Override
//    default ThisFluent is(Matcher<? super Property> matcher) {
//        return is().__(matcher);
//    }
//
//    @Override
//    default ThisFluent has(Matcher<? super Property> matcher) {
//        return has().__(matcher);
//    }
//
//    @Override
//    default ThisFluent not(Matcher<? super Property> matcher) {
//        return not().__(matcher);
//    }
//
//    @Override
//    default ThisFluent isNot(Matcher<? super Property> matcher) {
//        return is().not().__(matcher);
//    }
//
//    @Override
//    default ThisFluent hasNot(Matcher<? super Property> matcher) {
//        return has().not().__(matcher);
//    }
//    
//    @Override
//    default ThisFluent equalTo(Property value) {
//        return FluentPropertyImpl.this.__(IsEqual.equalTo(value));
//    }
//    
//    @Override
//    default ThisFluent is(Property value) {
//        return is().equalTo(value);
//    }
//    
//    @Override
//    default ThisFluent not(Property value) {
//        return not().equalTo(value);
//    }
//    
//    @Override
//    default ThisFluent isNot(Property value) {
//        return is().not().equalTo(value);
//    }
//
//    @Override
//    default ThisFluent all(Matcher<? super Property>... matchers) {
//        return FluentPropertyImpl.this.__(AndChainMatcher.all(matchers));
//    }
//
//    @Override
//    default ThisFluent any(Matcher<? super Property>... matchers) {
//        return FluentPropertyImpl.this.__(OrChainMatcher.any(matchers));
//    }
//
//    @Override
//    default ThisFluent none(Matcher<? super Property>... matchers) {
//        return FluentPropertyImpl.this.__(NOrChainMatcher.none(matchers));
//    }
//    
//    @Override
//    default ThisFluent matches(int count, Matcher<? super Property>... matchers) {
//        return FluentPropertyImpl.this.__(SomeOfChainMatcher.matches(count, matchers));
//    }
//    
//    @Override
//    default ThisFluent matches(Matcher<? super Integer> countMatcher, Matcher<? super Property>... matchers) {
//        return FluentPropertyImpl.this.__(SomeOfChainMatcher.matches(countMatcher, matchers));
//    }
//    
//    @Override
//    default ThisFluent matches(ChainFactory chainType, Matcher<? super Property>... matchers) {
//        return FluentPropertyImpl.this.__(chainType.create(matchers));
//    }
//
//    @Override
//    <P> FluentProperty<Value, P> __(MatchValueAdapter<? super Property, P> adapter);
//
//    @Override
//    default <P> FluentProperty<Value, P> has(MatchValueAdapter<? super Property, P> adapter) {
//        return has().__(adapter);
//    }
//
//    @Override
//    default <P> FluentProperty<Value, P> not(MatchValueAdapter<? super Property, P> adapter) {
//        return not().__(adapter);
//    }
//
//    @Override
//    default <P> FluentProperty<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter) {
//        return has().not().__(adapter);
//    }
//
//    @Override
//    default <P> Fluent<Value> __(MatchValueAdapter<? super Property, P> adapter, Matcher<? super P> matcher) {
//        return __(adapter.adapt(matcher));
//    }
//
//    @Override
//    default <P> Fluent<Value> has(MatchValueAdapter<? super Property, P> adapter, Matcher<? super P> matcher) {
//        return has().__(adapter, matcher);
//    }
    
//    /**
//     * Returns a {@link MatchesSome} that expects 
//     * {@code count} matchers to succeed.
//     * @param count
//     * @return matches-some fluent
//     */
//    MatchesSome<Value, Property> matches(int count);
//    
//    /**
//     * Returns a {@link MatchesSome} that expects {@code countMatcher} 
//     * to accept the number of successful matchers.
//     * @param countMatcher
//     * @return matches-some fluent
//     */
//    MatchesSome<Value, Property> matches(Matcher<? super Integer> countMatcher);
//    
//    /**
//     * Returns a {@link MatchesSome} that combines and applies matchers
//     * using the {@code chainType}.
//     * @param chainType
//     * @return matches-some
//     */
//    MatchesSome<Value, Property> matches(ChainFactory chainType);
//    
//    /**
//     * Combines matchers with a chain factory and applies them against 
//     * the fluent it was created from.
//     * @param <Value> type of the actual value
//     * @param <Property> type of the property to be matched
//     * @see #matches(int) 
//     * @see #matches(org.hamcrest.Matcher) 
//     * @see #matches(org.cthul.matchers.chain.ChainFactory) 
//     */
//    interface MatchesSome<Value, Property> {
//        
//        /**
//         * Builds and applies the combined matchers.
//         * @param matchers
//         * @return fluent
//         * @see #matches(int) 
//         * @see #matches(org.hamcrest.Matcher)  
//         * @see #matches(org.cthul.matchers.chain.ChainFactory) 
//         */
//        Fluent<Value> of(Matcher<? super Property>... matchers);
//    }
//
//    /**
//     * Adds a matcher to the fluent that matches only instances of {@code clazz}
//     * that are also matched by {@code matcher}.
//     * @param <Property2> expected type
//     * @param clazz expected type
//     * @param matcher the matcher
//     * @return fluent
//     */
//    <Property2 extends Property> Fluent<? extends Value> isA(Class<Property2> clazz, Matcher<? super Property2> matcher);
//
//    /**
//     * Immediately adds a matcher to the fluent that matches only 
//     * instances of {@code clazz}.
//     * <p>
//     * Returns a {@link IsA} that serves as a proxy for the fluent of the 
//     * actual value, but also allows to further match against the property,
//     * using the new type, via {@link IsA#that() that()}.
//     * @param <Property2> expected type
//     * @param clazz expected type
//     * @return isA fluent
//     */
//    <Property2 extends Property> IsA<? extends Value, Property2> isA(Class<Property2> clazz);
//
//    /**
//     * A proxy for a {@link Fluent} that allows to also match against some
//     * previously defined property via {@link IsA#that() that()}.
//     * @param <Value> type of the actual value
//     * @param <Property> type of the property to be matched
//     * @see #isA(java.lang.Class) 
//     */
//    interface IsA<Value, Property> extends Fluent<Value> {
//
//        /**
//         * Returns a {@link FluentProperty} that matches against the
//         * previously defined property.
//         * @return fluent property
//         * @see #thatIs() 
//         * @see #that(org.hamcrest.Matcher) 
//         * @see #thatIs(org.hamcrest.Matcher)
//         */
//        FluentProperty<Value, Property> that();
//
//        /**
//         * Equivalent to {@link #that() that()}{@link FluentProperty#is() \u2024is()}.
//         * @return fluent property
//         */
//        FluentProperty<Value, Property> thatIs();
//
//        /**
//         * Equivalent to {@link #that() that()}{@link FluentProperty#__(Matcher) \u2024_(matcher)}.
//         * @param matcher the matcher
//         * @return fluent 
//         */
//        Fluent<Value> that(Matcher<? super Property> matcher);
//
//        /**
//         * Equivalent to {@link #that() that()}{@link FluentProperty#is() \u2024is()}{@link FluentProperty#__(Matcher) \u2024_(matcher)}.
//         * @param matcher the matcher
//         * @return fluent 
//         */
//        Fluent<Value> thatIs(Matcher<? super Property> matcher);
//    }

}
