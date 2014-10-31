package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentProperty;
import org.cthul.matchers.fluent.ext.ExtendedAdapter;
import org.cthul.matchers.fluent.ext.ExtendedAdapter.Matchable;
import org.cthul.matchers.object.CIs;
import org.hamcrest.Matcher;

/**
 *
 */
public class FluentPropertyBuilder<Value, TheFluent, This extends FluentPropertyBuilder<Value, TheFluent, This>>
                extends AbstractFluentPropertyBuilder<Value, TheFluent, TheFluent, This> {
    
    private final Matchable<? extends Value, TheFluent> matchable;

    public FluentPropertyBuilder(Matchable<? extends Value, TheFluent> matchable) {
        this.matchable = matchable;
    }
    
    @Override
    protected TheFluent _applyMatcher(Matcher<? super Value> matcher, String prefix, boolean not) {
        matcher = CIs.wrap(prefix, not, matcher);
        return matchable.apply(matcher);
    }

    @Override
    public <Property2 extends Value> FluentProperty<Property2, ? extends TheFluent> as(Class<Property2> clazz) {
        return _as(clazz);
    }
}
