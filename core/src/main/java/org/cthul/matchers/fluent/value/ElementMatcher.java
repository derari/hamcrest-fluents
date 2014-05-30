package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

/**
 * A Hamcrest Matcher that accepts {@link Element}s of a type.
 * <p>
 * The matcher accepts any type of element, 
 * the value type has to be checked explicitly.
 * <p>
 * This interface is intended for internal communication between 
 * {@link MatchValueAdapter}s.
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
    
    /**
     * A {@code MatchResult} that provides a specialized {@link Mismatch}.
     * @param <Value> 
     */
    interface Result<Value> extends MatchResult<Value> {
        
        @Override
        ElementMatcher.Mismatch<Value> getMismatch();
    }
    
    /**
     * A {@link MatchResult.Mismatch} that can fill an {@link ExpectationDescription}.
     * @param <Value> 
     */
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
         * Adds an expectation. The index sets the order in which it will be
         * written to the result. {@code -1} will append to the beginning.
         * @param index
         * @param expected 
         */
        void addExpected(int index, SelfDescribing expected);
    }
}
