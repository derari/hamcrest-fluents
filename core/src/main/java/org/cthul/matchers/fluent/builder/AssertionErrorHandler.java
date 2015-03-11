package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.ElementMatcher;

/**
 * Handles mismatches by throwing {@link AssertionError}s.
 */
public class AssertionErrorHandler extends FailureHandlerBase {

    private static final AssertionErrorHandler INSTANCE = new AssertionErrorHandler();

    public static AssertionErrorHandler instance() {
        return INSTANCE;
    }
    
    @Override
    public <T> void mismatch(String reason, MatchValue<T> actual, ElementMatcher<T> matcher) {
        throw new AssertionError(getMismatchString(reason, actual));
    }

}
