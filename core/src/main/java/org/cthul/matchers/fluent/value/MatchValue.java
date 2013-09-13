package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
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
    
    interface Result<Value> extends MatchResult<Value> {
        
        @Override
        MatchValue.Mismatch<Value> getMismatch();
    }
    
    interface Mismatch<Value> extends MatchValue.Result<Value>, MatchResult.Mismatch<Value> {
        
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
    interface ElementMatcher<Value> extends QuickDiagnosingMatcher<Element<?>> {
        
        @Override
        <I> MatchValue.Result<I> matchResult(I item);
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
