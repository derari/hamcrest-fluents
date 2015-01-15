package org.cthul.matchers.fluent.builder;

import org.hamcrest.Matcher;

/**
 * Allows step implementations to pass matchers to their parent fluent.
 * @param <Value> value type
 * @param <TheFluent> parent fluent type
 */
public interface Matchable<Value, TheFluent> {

    TheFluent apply(Matcher<? super Value> matcher);
}
