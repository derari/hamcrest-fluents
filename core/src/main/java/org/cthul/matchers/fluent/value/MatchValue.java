package org.cthul.matchers.fluent.value;

import java.util.Collection;
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
public interface MatchValue<Value> extends MatchResult<MatchValue<Value>> {
    
    @Override
    Mismatch<Value> getMismatch();
    
    /**
     * Applies the matcher.
     * @param matcher the matcher
     * @return true iff value is valid
     */
    boolean matches(ElementMatcher<Value> matcher);
    
    MatchValue<Value> matchResult(ElementMatcher<Value> matcher);
        
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
    
    interface Mismatch<Value> extends MatchResult.Mismatch<MatchValue<Value>> {
        
        /**
         * Describes what was expected of the value.
         * @param description 
         */
        void describeExpected(ExpectationDescription description);
    }
    
    /**
     * A single element of a {@link MatchValue}.
     * <p>
     * If two elements are equal, they represent the same underlying value.
     * @param <Value> value type
     */
    interface Element<Value> {
        
        Value value();
        
    }
    
    /**
     * A Hamcrest Matcher that accepts {@link Element}s of a type.
     * @param <Value> value type
     * @see org.cthul.matchers.fluent.value.ElementMatcher
     */
    interface ElementMatcher<Value> extends Matcher<Element<?>> {
        
        /**
         * Generates a description that explains what would have been expected
         * of an element to be matched successfully.
         * @param e element that was rejected
         * @param description the description to append to
         */
        void describeExpected(Element<?> e, ExpectationDescription description);
        
    }
    
    /**
     * A description that consists of multiple expectation descriptions,
     * which will be combined for the end result.
     * Can be expected to remove duplicates.
     */
    interface ExpectationDescription extends Description {
        
        /**
         * 
         * @param index
         * @param expected 
         */
        void addExpected(int index, SelfDescribing expected);
        
    }
}
