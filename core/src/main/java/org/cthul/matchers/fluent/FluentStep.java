package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.ext.ExtensibleFluent;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent that matches a value and returns to the original fluent chain.
 * @param <Value> value type
 * @param <TheFluent> original fluent type
 */
public interface FluentStep<Value, TheFluent> {

    /**
     * Prepends "is" to the next matcher's description.
     * @return this
     */
    FluentStep<Value, ? extends TheFluent> is();

    /**
     * Prepends "has" to the next matcher's description.
     * @return this
     */
    FluentStep<Value, ? extends TheFluent> has();

    /**
     * Negates the next matcher's match result and 
     * prepends "not" to its description.
     * @return this
     */
    FluentStep<Value, ? extends TheFluent> not();
    
/**
     * Equivalent to {@link #is() is()}{@link #not() .not()}.
     * @return this
     */
    FluentStep<Value, ? extends TheFluent> isNot();

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}.
     * @return this
     */
    FluentStep<Value, ? extends TheFluent> hasNot();

    /**
     * Applies a matcher and returns to the original fluent chain.
     * <p>
     * Side effects of applying a matcher are implementation specific
     * @param matcher the matcher
     * @return the fluent
     */
    TheFluent __(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #is() is()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return the fluent
     */
    TheFluent is(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return the fluent
     */
    TheFluent has(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #not() not()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return the fluent
     */
    TheFluent not(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #is() is()}{@link #not() .not()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return the fluent
     */
    TheFluent isNot(Matcher<? super Value> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}{@link #__(Matcher) .__(matcher)}.
     * @param matcher the matcher
     * @return the fluent
     */
    TheFluent hasNot(Matcher<? super Value> matcher);
    
    /**
     * Adds a matcher that checks if the property is 
     * {@link org.hamcrest.core.IsEqual#equalTo(Object) equalTo}
     * the given value.
     * @param value the value
     * @return the fluent
     * @see org.hamcrest.core.IsEqual#equalTo(Object) 
     */
    TheFluent equalTo(Value value);
    
    /**
     * Equivalent to {@link #is() is()}{@link #equalTo(Object) .equalTo(value)}. 
     * @param value the value
     * @return the fluent
     */
    TheFluent is(Value value);
    
    /**
     * Equivalent to {@link #not() not()}{@link #equalTo(Object) .equalTo(value)}.
     * @param value the value
     * @return the fluent
     */
    TheFluent not(Value value);
    
    /**
     * Equivalent to {@link #is() is()}{@link #not() .not()}{@link #equalTo(Object) .equalTo(value)}.
     * @param value the value
     * @return the fluent
     */
    TheFluent isNot(Value value);

    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the conjunction of {@code matchers}.
     * @param matchers the matchers
     * @return the fluent
     */
    TheFluent allOf(Matcher<? super Value>... matchers);

    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the disjunction of {@code matchers}.
     * @param matchers the matchers
     * @return the fluent
     */
    TheFluent anyOf(Matcher<? super Value>... matchers);

    /**
     * Equivalent to calling {@link #__(Matcher) __(matcher)}
     * with the joint denial of {@code matchers}.
     * @param matchers the matchers
     * @return the fluent
     */
    TheFluent noneOf(Matcher<? super Value>... matchers);
    
//    /**
//     * Equivalent to calling {@link #__(Matcher) __(matcher)}
//     * with a matcher that matches when exactly {@code count} of 
//     * {@code matchers} match.
//     * @param count
//     * @param matchers
//     * @return the fluent
//     */
//    TheFluent matches(int count, Matcher<? super Value>... matchers);
//    
//    /**
//     * Equivalent to calling {@link #__(Matcher) __(matcher)}
//     * with a matcher that succeeds when the number of succeeding
//     * {@code matchers} is matched by {@code countMatcher}.
//     * @param countMatcher
//     * @param matchers
//     * @return the fluent
//     */
//    TheFluent matches(Matcher<? super Integer> countMatcher, Matcher<? super Value>... matchers);
//    
//    /**
//     * Equivalent to calling {@link #__(Matcher) __(matcher)}
//     * with the result of {@code chainType.create(matchers)}.
//     * @param chainType
//     * @param matchers
//     * @return the fluent
//     */
//    TheFluent matches(ChainFactory chainType, Matcher<? super Value>... matchers);

    /**
     * Returns a {@link FluentStep} that uses the {@code adapter} to
     * apply matchers against the original value.
     * @param <NextValue> adapted value type
     * @param adapter the adapter
     * @return new fluent step
     */
    <NextValue> FluentStep<NextValue, ? extends TheFluent> __(MatchValueAdapter<? super Value, ? extends NextValue> adapter);

    /**
     * Equivalent to {@link #has() has()}{@link #__(MatchValueAdapter) .__(adapter)}.
     * @param <NextValue> adapted value type
     * @param adapter the adapter
     * @return new fluent step
     */
    <NextValue> FluentStep<NextValue, ? extends TheFluent> has(MatchValueAdapter<? super Value, ? extends NextValue> adapter);

    /**
     * Equivalent to {@link #not() not()}{@link #__(MatchValueAdapter) .__(adapter)}.
     * @param <NextValue> adapted value type
     * @param adapter the adapter
     * @return new fluent step
     */
    <NextValue> FluentStep<NextValue, ? extends TheFluent> not(MatchValueAdapter<? super Value, ? extends NextValue> adapter);

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}{@link #__(MatchValueAdapter) .__(adapter)}.
     * @param <NextValue> adapted value type
     * @param adapter the adapter
     * @return new fluent step
     */
    <NextValue> FluentStep<NextValue, ? extends TheFluent> hasNot(MatchValueAdapter<? super Value, ? extends NextValue> adapter);

    /**
     * Equivalent to {@link #__(Matcher) __(adapter.adapt(matcher)}.
     * @param <NextValue> adapted value type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return the fluent
     */
    <NextValue> TheFluent __(MatchValueAdapter<? super Value, ? extends NextValue> adapter, Matcher<? super NextValue> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #__(Matcher) .__(adapter.adapt(matcher)}.
     * @param <NextValue> adapted value type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return the fluent
     */
    <NextValue> TheFluent has(MatchValueAdapter<? super Value, ? extends NextValue> adapter, Matcher<? super NextValue> matcher);

    /**
     * Equivalent to {@link #not() not()}{@link #__(Matcher) .__(adapter.adapt(matcher)}.
     * @param <NextValue> adapted value type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return the fluent
     */
    <NextValue> TheFluent not(MatchValueAdapter<? super Value, ? extends NextValue> adapter, Matcher<? super NextValue> matcher);

    /**
     * Equivalent to {@link #has() has()}{@link #not() .not()}{@link #__(Matcher) .__(adapter.adapt(matcher)}.
     * @param <NextValue> adapted value type
     * @param adapter the adapter
     * @param matcher the matcher
     * @return the fluent
     */
    <NextValue> TheFluent hasNot(MatchValueAdapter<? super Value, ? extends NextValue> adapter, Matcher<? super NextValue> matcher);

    /**
     * Adds a matcher to the fluent that matches only instances of {@code clazz}.
     * @param <Value2> expected type
     * @param clazz expected type
     * @return the fluent
     */
    <Value2 extends Value> TheFluent hasType(Class<Value2> clazz);

    /**
     * Adds a matcher to the fluent that matches only instances of {@code clazz}
     * that are also matched by {@code matcher}.
     * @param <Value2> expected type
     * @param clazz expected type
     * @param matcher the matcher
     * @return the fluent
     */
    <Value2 extends Value> TheFluent isA(Class<Value2> clazz, Matcher<? super Value2> matcher);

    /**
     * Returns a fluent step that checks the value is an instance of {@code clazz}
     * before applying a matcher.
     * @param <Value2> expected type
     * @param clazz expected type 
     * @return fluent step
     */
    <Value2 extends Value> FluentStep<Value2, ? extends TheFluent> as(Class<Value2> clazz);
    
    AndChain<Value, ? extends TheFluent, ?> all();
    
    AndChain<Value, ? extends TheFluent, ?> both();
    
    AndChain<Value, ? extends TheFluent, ?> both(Matcher<? super Value> matcher);
    
    interface AndChain<Value, TheFluent, This extends AndChain<Value, TheFluent, This>> extends ExtensibleFluent<Value, This> {

        FluentStep<Value, TheFluent> and();
        
        TheFluent and(Matcher<? super Value> matcher);
    }
    
    OrChain<Value, ? extends TheFluent, ?> either();
    
    OrChain<Value, ? extends TheFluent, ?> either(Matcher<? super Value> matcher);
    
    interface OrChain<Value, TheFluent, This extends OrChain<Value, TheFluent, This>> extends ExtensibleFluent<Value, This> {

        FluentStep<Value, TheFluent> or();
        
        TheFluent or(Matcher<? super Value> matcher);
    }
}
