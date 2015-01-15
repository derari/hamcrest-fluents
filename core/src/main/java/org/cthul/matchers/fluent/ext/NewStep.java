package org.cthul.matchers.fluent.ext;

import org.cthul.matchers.fluent.builder.Matchable;

/**
 * Factory for {@link ExtensibleFluentStep}s.
 * @param <NextValue> value type of step
 * @param <TheFluent> original fluent type
 * @param <TheStep> step type
 */
public interface NewStep<NextValue, TheFluent, TheStep> {

    TheStep create(Matchable<NextValue, TheFluent> matchable);
}
