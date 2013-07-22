package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.FluentPropertyAssert;
import org.cthul.matchers.fluent.value.AdaptingMatcher;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractPropertyAssertBuilder
                <Value, Property, ThisFluent extends FluentAssert<Value>,
                 This extends FluentPropertyAssert<Value, Property>>
                extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>
                implements FluentPropertyAssert<Value, Property> {

    @Override
    protected <P> FluentPropertyAssert<Value, P> _newProperty(MatchValueAdapter<? super Property, P> adapter, String prefix, boolean not) {
        return new AssertPropertyBuilder<>(adapter, prefix, not);
    }

    @Override
    public <P> FluentPropertyAssert<Value, P> _(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentPropertyAssert) super._(adapter);
    }
    
    @Override
    public <P> FluentPropertyAssert<Value, P> has(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentPropertyAssert) super.has(adapter);
    }
    
    @Override
    public <P> FluentPropertyAssert<Value, P> not(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentPropertyAssert) super.not(adapter);
    }
    
    @Override
    public <P> FluentPropertyAssert<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter) {
        return (FluentPropertyAssert) super.hasNot(adapter);
    }

    @Override
    public Both<Value, Property, ThisFluent> both(Matcher<? super Property> matcher) {
        return new Both<>(this, matcher);
    }
    
    protected static class Both<Value, Property,
                                ThisFluent extends FluentAssert<Value>>
                        extends AbstractFluentPropertyBuilder.Both<Value, Property, ThisFluent>
                        implements FluentPropertyAssert.Both<Value, Property> {

        public Both(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property> first) {
            super(property, first);
        }
    }

    @Override
    public Either<Value, Property, ThisFluent> either(Matcher<? super Property>... matchers) {
        return new Either<>(this, matchers);
    }
    
    protected static class Either<Value, Property,
                                ThisFluent extends FluentAssert<Value>>
                        extends AbstractFluentPropertyBuilder.Either<Value, Property, ThisFluent>
                        implements FluentPropertyAssert.Either<Value, Property> {

        public Either(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property>... matchers) {
            super(property, matchers);
        }
    }
    
    @Override
    public Neither<Value, Property, ThisFluent> neither(Matcher<? super Property>... matchers) {
        return new Neither<>(this, matchers);
    }
    
    protected static class Neither<Value, Property,
                                ThisFluent extends FluentAssert<Value>>
                        extends AbstractFluentPropertyBuilder.Neither<Value, Property, ThisFluent>
                        implements FluentPropertyAssert.Neither<Value, Property> {

        public Neither(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property>... matchers) {
            super(property, matchers);
        }
    }

    @Override
    public <Property2 extends Property> FluentAssert<? extends Value> isA(Class<Property2> clazz, Matcher<? super Property2> matcher) {
        return (FluentAssert) super.isA(clazz, matcher);
    }

    @Override
    public <Property2 extends Property> FluentPropertyAssert.IsA<? extends Value, Property2> isA(Class<Property2> clazz) {
        return (FluentPropertyAssert.IsA) super.isA(clazz);
    }

    @Override
    protected Class<?> getIsAInterface() {
        return FluentPropertyAssert.IsA.class;
    }
    
    protected class AssertPropertyBuilder
                    <Property2, This2 extends AssertPropertyBuilder<Property2, This2>>
                    extends AbstractPropertyAssertBuilder<Value, Property2, ThisFluent, This2> 
                    implements FluentPropertyAssert<Value, Property2> {
        
        private final MatchValueAdapter<? super Property, Property2> adapter;
        private final String flPrefix;
        private final boolean flNot;

        public AssertPropertyBuilder(MatchValueAdapter<? super Property, Property2> adapter, String prefix, boolean not) {
            this.adapter = adapter;
            this.flPrefix = prefix;
            this.flNot = not;
        }

        @Override
        protected ThisFluent _applyMatcher(Matcher<? super Property2> matcher, String prefix, boolean not) {
            Matcher<? super Property> m = new AdaptingMatcher<>(adapter, matcher, prefix, not);
            return AbstractPropertyAssertBuilder.this._applyMatcher(m, flPrefix, flNot);
        }      
        
        @Override
        protected ThisFluent _updateMatcher(Matcher<? super Property2> matcher, String prefix, boolean not) {
            Matcher<? super Property> m = new AdaptingMatcher<>(adapter, matcher, prefix, not);
            return AbstractPropertyAssertBuilder.this._updateMatcher(m, flPrefix, flNot);
        }
    }
}
