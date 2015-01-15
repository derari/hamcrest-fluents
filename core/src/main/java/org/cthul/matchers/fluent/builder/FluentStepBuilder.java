package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.object.CIs;
import org.hamcrest.Matcher;

/**
 * A fluent step that applies matchers against a {@link Matchable}.
 * @param <Value> value type
 * @param <TheFluent> fluent type
 * @param <This> this type
 */
public class FluentStepBuilder<Value, TheFluent, This extends FluentStepBuilder<Value, TheFluent, This>>
                extends AbstractFluentStepBuilder<Value, TheFluent, TheFluent, This> {
    
    private final Matchable<? extends Value, TheFluent> matchable;

    public FluentStepBuilder(Matchable<? extends Value, TheFluent> matchable) {
        this.matchable = matchable;
    }
    
    @Override
    protected TheFluent _apply(Matcher<? super Value> matcher, String prefix, boolean not) {
        matcher = CIs.wrap(prefix, not, matcher);
        return matchable.apply(matcher);
    }
}
