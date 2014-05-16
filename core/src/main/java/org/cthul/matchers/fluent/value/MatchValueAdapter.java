package org.cthul.matchers.fluent.value;

import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.adapters.SimpleAnyOfAdapter;
import org.cthul.matchers.fluent.adapters.SimpleEachOfAdapter;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 * Creates {@link MatchValue}s from plain values.
 * <p>
 * A MatchValueAdapter represents an aspect of some type, 
 * like the length of strings or each element of lists.
 * It can then be used to create {@link MatchValue}s that represent
 * this aspect of a concrete value.
 * <p>
 * Every implementation should extend {@link MatchValueAdapterBase} for
 * forward compatibility, but there are other abstract implementations which
 * might be better starting points.
 * @param <Value>
 * @param <Property>
 * @see SimpleAdapter
 * @see SimpleAnyOfAdapter
 * @see SimpleEachOfAdapter
 */
public interface MatchValueAdapter<Value, Property> extends SelfDescribing {
    
    /**
     * Converts a plain value into a match value.
     * @param value source value
     * @return match value
     */
    MatchValue<Property> adapt(Object value);
    
    /**
     * Creates a match value that will have its elements derived from
     * the elements of the source match value.
     * @param value source value
     * @return match value
     */
    MatchValue<Property> adapt(MatchValue<? extends Value> value);
    
    /**
     * Creates a match value adapter that takes the match values from another
     * adapter and adapts them with this adapter.
     * @param <Value0> input type of resulting adapter
     * @param adapter source adapter
     * @return match value adapter
     */
    <Value0> MatchValueAdapter<Value0, Property> adapt(MatchValueAdapter<Value0, ? extends Value> adapter);
    
    /**
     * Returns a matcher that builds a {@link MatchValue} using this adapter,
     * and verifies it with the given matcher.
     * @param matcher the matcher
     * @return adapted matcher
     */
    Matcher<Value> adapt(Matcher<? super Property> matcher);
    
    /**
     * Equivalent to {@code adapter.adapt(this)};
     * @param <Property2>
     * @param adapter
     * @return match value adapter
     */
    <Property2> MatchValueAdapter<Value, Property2> get(MatchValueAdapter<? super Property, Property2> adapter);
    
}
