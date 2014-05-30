package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.ElementMatcher;

/**
 * Handles mismatches of fluent assertions.
 */
public interface FailureHandler {

    public static final AssertionErrorHandler ASSERT = AssertionErrorHandler.INSTANCE;

    <T> void mismatch(String reason, MatchValue<T> item, ElementMatcher<T> matcher);
}
