package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 * A value that can be matched. 
 * <p>
 * Can consist of multiple elements that will be matched individually.
 * The MatchValue tracks which elements matched successfully and is
 * able to provide messages that describe its state.
 * @param <Value> element type
 */
public interface MatchValue<Value> extends SelfDescribing {
    
    boolean matches(Matcher<? super Value> matcher);
    
    MatchResult<?> matchResult(Matcher<? super Value> matcher);
    
    boolean matches(ElementMatcher<? super Value> matcher);
    
    MatchResult<?> matchResult(ElementMatcher<? super Value> matcher);
    
    boolean matched();
    
    MatchResult<?> matchResult();
        
    /**
     * Describes the value.
     * @param description 
     */
    void describeValue(Description description);
        
    /**
     * Describes the value's type.
     * @param description 
     */
    void describeValueType(Description description);
    
    /**
     * Equivalent to {@code adapter.adapt(this)}.
     * @param <Property>
     * @param adapter
     * @return adapted match value
     */
    <Property> MatchValue<Property> get(MatchValueAdapter<? super Value, Property> adapter);
}
