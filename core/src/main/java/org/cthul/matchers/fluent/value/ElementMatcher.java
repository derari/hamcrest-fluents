package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

/**
 * A Hamcrest Matcher that accepts {@link Element}s of a type.
 * @param <Value> value type
 * @see org.cthul.matchers.fluent.value.ElementMatcher
 */
public interface ElementMatcher<Value> extends QuickDiagnosingMatcher<ElementMatcher.Element<?>> {

    @Override
    <I> Result<I> matchResult(I item);
    
    /**
     * A single element.
     * <p>
     * If two elements are equal, they represent the same underlying value.
     * @param <Value> value type
     */
    interface Element<Value> {
        
        Value value();
    }
    
    interface Result<Value> extends MatchResult<Value> {
        
        @Override
        ElementMatcher.Mismatch<Value> getMismatch();
    }
    
    interface Mismatch<Value> extends Result<Value>, MatchResult.Mismatch<Value> {
        
        /**
         * Describes what was expected of the value.
         * @param description 
         */
        void describeExpected(ExpectationDescription description);
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
