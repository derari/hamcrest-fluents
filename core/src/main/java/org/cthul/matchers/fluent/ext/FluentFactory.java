package org.cthul.matchers.fluent.ext;

import org.cthul.matchers.fluent.builder.Matchable;

/**
 * Factory for {@link ExtensibleFluent}s.
 * @param <Value> value type
 * @param <TheFluent> fluent type
 */
public interface FluentFactory<Value, TheFluent> {

    TheFluent newFluent(Matchable<? extends Value, ?> value);
}
