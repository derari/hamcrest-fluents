package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.FluentPropertyMatcher;
import org.cthul.matchers.fluent.value.AdaptingMatcher;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractPropertyMatcherBuilder
                <Value, Property, Match, ThisFluent extends FluentMatcher<Value, Match>,
                 This extends AbstractPropertyMatcherBuilder<Value, Property, Match, ThisFluent, This>>
                extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>
                implements FluentPropertyMatcher<Value, Property, Match> {

    @Override
    protected <P> FluentPropertyMatcher<Value, P, Match> _newProperty(MatchValueAdapter<? super Property, P> adapter, String prefix, boolean not) {
        return new MatcherPropertyBuilder<>(adapter, prefix, not);
    }

    @Override
    public <P> FluentPropertyMatcher<Value, P, Match> _(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentPropertyMatcher) super._(adapter);
    }
    
    @Override
    public <P> FluentPropertyMatcher<Value, P, Match> has(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentPropertyMatcher) super.has(adapter);
    }
    
    @Override
    public <P> FluentPropertyMatcher<Value, P, Match> not(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentPropertyMatcher) super.not(adapter);
    }
    
    @Override
    public <P> FluentPropertyMatcher<Value, P, Match> hasNot(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentPropertyMatcher) super.hasNot(adapter);
    }
    
    @Override
    public <Property2 extends Property> FluentMatcher<? extends Value, Match> isA(Class<Property2> clazz, Matcher<? super Property2> matcher) {
        return (FluentMatcher) super.isA(clazz, matcher);
    }

    @Override
    public <Property2 extends Property> FluentPropertyMatcher.IsA<? extends Value, Property2, Match> isA(Class<Property2> clazz) {
        return (FluentPropertyMatcher.IsA) super.isA(clazz);
    }

    @Override
    protected Class<?> getIsAInterface() {
        return FluentPropertyMatcher.IsA.class;
    }
    
    protected class MatcherPropertyBuilder
                    <Property2, This2 extends MatcherPropertyBuilder<Property2, This2>>
                    extends AbstractPropertyMatcherBuilder<Value, Property2, Match, ThisFluent, This2> 
                    implements FluentPropertyMatcher<Value, Property2, Match> {
        
        private final MatchValueAdapter<? super Property, Property2> adapter;
        private final String flPrefix;
        private final boolean flNot;

        public MatcherPropertyBuilder(MatchValueAdapter<? super Property, Property2> adapter, String prefix, boolean not) {
            this.adapter = adapter;
            this.flPrefix = prefix;
            this.flNot = not;
        }

        @Override
        protected ThisFluent _applyMatcher(Matcher<? super Property2> matcher, String prefix, boolean not) {
            Matcher<? super Property> m = new AdaptingMatcher<>(adapter, matcher, prefix, not);
            return AbstractPropertyMatcherBuilder.this._applyMatcher(m, flPrefix, flNot);
        }
    }
}