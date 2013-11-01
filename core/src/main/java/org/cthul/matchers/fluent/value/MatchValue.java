package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 * A value that can be matched. 
 * <p>
 * The value can be complex and consist of multiple elements of type 
 * {@code Value} that will be matched individually. The {@code MatchValue} 
 * tracks which elements matched successfully and is able to provide a 
 * {@link #matchResult() MatchResult} that describes its state.
 * <p>
 * Once a match value has become invalid, it will not become valid again,
 * and subsequent calls to {@link #matches(ElementMatcher)} may even be ignored.
 * <p>
 * {@code MatchValue}s should not be expected to be thread-safe. Furthermore,
 * {@code MatchResult}s provided by a {@code MatchValue} may change their state
 * when matchers are applied concurrently.
 * <p>
 * Usually, it will not be necessary to implement this interface, unless
 * one implements a {@link MatchValueAdapter}. See the docs there for starting
 * points. For forward compatibility, all implementations should extend 
 * {@link MatchValueBase}.
 * @param <Value> element type
 */
public interface MatchValue<Value> extends SelfDescribing {
    
    /**
     * Applies a matcher, which may cause the match value to become invalid.
     * The matcher may be called several times with different elements.
     * @param matcher
     * @return {@code true} iff the match value is valid
     */
    boolean matches(Matcher<? super Value> matcher);
    
    /**
     * 
     * @param matcher
     * @return match result
     * @see #matches(org.hamcrest.Matcher) 
     * @see #matchResult() 
     */
    MatchResult<?> matchResult(Matcher<? super Value> matcher);
    
    /**
     * Applies a matcher, which may cause the match value to become invalid.
     * The matcher may be called several times with different elements.
     * @param matcher
     * @return {@code true} iff the match value is valid
     */
    boolean matches(ElementMatcher<? super Value> matcher);
    
    /**
     * @param matcher
     * @return match result
     * @see #matches(org.cthul.matchers.fluent.value.ElementMatcher) 
     * @see #matchResult() 
     */
    MatchResult<?> matchResult(ElementMatcher<? super Value> matcher);
    
    /**
     * Returns whether the value is considered valid.
     * @return {@code true} iff the match value is valid
     */
    boolean matched();
    
    /**
     * Returns a {@code MatchResult} that describes the current matching state.
     * The match result's value will be the underlying value.
     * @return match result
     */
    MatchResult<?> matchResult();
        
    /**
     * Describes this value.
     * @param description 
     */
    void describeValue(Description description);
        
    /**
     * Describes this value's type.
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
