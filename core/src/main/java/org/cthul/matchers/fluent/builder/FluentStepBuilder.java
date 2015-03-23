package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.ext.ExtensionAdapter;
import org.cthul.matchers.fluent.ext.ExtensionFactory;
import org.cthul.matchers.fluent.ext.Extensions;
import org.cthul.matchers.fluent.ext.StepFactory;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
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

    public static <Value, TheFluent> ExtensionFactory<Value, FluentStepBuilder<Value, TheFluent, ?>, TheFluent, FluentStepBuilder<Value, TheFluent, ?>> factory() {
        return FACTORY;
    }
    
    public static <Value, NextValue, TheFluent> ExtensionAdapter<Value, NextValue, FluentStepBuilder<NextValue, TheFluent, ?>, TheFluent, FluentStepBuilder<NextValue, TheFluent, ?>> adapter(MatchValueAdapter<? super Value, ? extends NextValue> adapter) {
        return FluentStepBuilder.<NextValue, TheFluent>factory().adaptTo(adapter);
    }
    
    private static final ExtensionFactory FACTORY = Extensions.factory(new StepFactory() {
        @Override
        public Object newStep(Matchable matchable) {
            return new FluentStepBuilder(matchable);
        }
    });
}
