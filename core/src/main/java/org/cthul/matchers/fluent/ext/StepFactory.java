package org.cthul.matchers.fluent.ext;

import org.cthul.matchers.fluent.builder.Matchable;

/**
 * Factory for {@link ExtensibleFluentStep}s.
 * @param <Value> value type of step
 * @param <TheFluent> original fluent type
 * @param <TheStep> step type
 */
public interface StepFactory<Value, TheFluent, TheStep> {

    TheStep newStep(Matchable<? extends Value, TheFluent> matchable);
}
