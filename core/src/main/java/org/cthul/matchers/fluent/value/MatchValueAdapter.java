package org.cthul.matchers.fluent.value;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Creates {@link MatchValue} from a plain value.
 * <p>
 * A MatchValueAdapter represents an aspect of some type, 
 * like the length of strings or each element of lists.
 * It can then be used to create {@link MatchValue}s that represent
 * this aspect of a concrete value.
 */
public interface MatchValueAdapter<Value, Property> {
    
    /**
     * Converts a plain value into a match value.
     * @param value the value
     * @return match value
     */
    MatchValue<Property> adapt(Value value);
    
    /**
     * Creates a match value that will have its elements derived from
     * the elements of the source match value.
     * @param value the value
     * @return match value
     */
    MatchValue<Property> adapt(MatchValue<Value> value);
    
    /**
     * Creates a match value adapter that takes the match values from another
     * adapter and adapts them with this adapter.
     * @param <Value0> input type of resulting adapter
     * @param adapter source adapter
     * @return match value adapter
     */
    <Value0> MatchValueAdapter<Value0, Property> adapt(MatchValueAdapter<Value0, Value> adapter);
    
    void describeMatcher(Matcher<? super Property> matcher, Description description);
    
}
