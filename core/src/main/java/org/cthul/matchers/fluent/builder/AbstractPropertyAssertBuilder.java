package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.fluent.FluentAssert;
import org.cthul.matchers.fluent.FluentProperty;
import org.cthul.matchers.fluent.FluentPropertyAssert;
import org.cthul.matchers.fluent.ext.ExtendableFluentPropertyAssert;
import org.cthul.matchers.fluent.ext.ExtendedAdapter.Matchable;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.cthul.matchers.object.CIs;
import org.cthul.matchers.object.InstanceOf;
import org.hamcrest.Matcher;

/**
 * Base class for all fluent assert implementations
 * @param <Value> base value type
 * @param <Property> property type
 * @param <ThisFluent> type of fluent this property belongs to
 * @param <This> fluent interface implemented by this class
 */
public abstract class AbstractPropertyAssertBuilder
                <Value, Property, ThisFluent extends FluentAssert<Value>,
                 This extends AbstractPropertyAssertBuilder<Value, Property, ThisFluent, This>>
                extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>
                implements FluentPropertyAssert<Value, Property>,
                ExtendableFluentPropertyAssert<Value, Property, ThisFluent, This> {

    @Override
    protected <P> FluentPropertyAssert<Value, P> _newProperty(Matchable<P, ThisFluent> matchable) {
        return new AssertPropertyBuilder<>(matchable);
    }

    @Override
    public <NextProperty> FluentPropertyAssert<Value, NextProperty> __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        return (FluentPropertyAssert) super.__(adapter);
    }
    
    @Override
    public <NextProperty> FluentPropertyAssert<Value, NextProperty> has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        return (FluentPropertyAssert) super.has(adapter);
    }
    
    @Override
    public <NextProperty> FluentPropertyAssert<Value, NextProperty> not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        return (FluentPropertyAssert) super.not(adapter);
    }
    
    @Override
    public <NextProperty> FluentPropertyAssert<Value, NextProperty> hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
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
    public MatchesSome<Value, Property, ThisFluent> matches(int count) {
        return new MatchesSome<>(this, count);
    }

    @Override
    public MatchesSome<Value, Property, ThisFluent> matches(Matcher<? super Integer> countMatcher) {
        return new MatchesSome<>(this, countMatcher);
    }

    @Override
    public MatchesSome<Value, Property, ThisFluent> matches(ChainFactory chainType) {
        return new MatchesSome<>(this, chainType);
    }
    
    protected static class MatchesSome<Value, Property,
                                ThisFluent extends FluentAssert<Value>>
                        extends AbstractFluentPropertyBuilder.MatchesSome<Value, Property, ThisFluent>
                        implements FluentPropertyAssert.MatchesSome<Value, Property> {

        public MatchesSome(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, int count) {
            super(property, count);
        }
        public MatchesSome(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Integer> countMatcher) {
            super(property, countMatcher);
        }
        public MatchesSome(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, ChainFactory chainType) {
            super(property, chainType);
        }
    }

    @Override
    public <Property2 extends Property> FluentPropertyAssert.IsA<Value, Property2> isA(Class<Property2> clazz) {
        return (FluentPropertyAssert.IsA) super.isA(clazz);
    }

    @Override
    public <Property2 extends Property> FluentPropertyAssert<? extends Value, Property2> as(Class<Property2> clazz) {
        _match(InstanceOf.isA(clazz));
        return (FluentPropertyAssert) this;
    }

    @Override
    protected Class<?> getIsAInterface() {
        return FluentPropertyAssert.IsA.class;
    }
    
    protected static class AssertPropertyBuilder
                    <Value, Property, 
                     ThisFluent extends FluentAssert<Value>,
                     This extends AssertPropertyBuilder<Value, Property, ThisFluent, This>>
                     extends AbstractPropertyAssertBuilder<Value, Property, ThisFluent, This> {
        
        private final Matchable<Property, ThisFluent> matchable;

        public AssertPropertyBuilder(Matchable<Property, ThisFluent> matchable) {
            this.matchable = matchable;
        }
        
        @Override
        protected ThisFluent _applyMatcher(Matcher<? super Property> matcher, String prefix, boolean not) {
            matcher = CIs.wrap(prefix, not, matcher);
            return matchable.apply(matcher);
        }

        @Override
        protected ThisFluent _updateMatcher(Matcher<? super Property> matcher, String prefix, boolean not) {
            matcher = CIs.wrap(prefix, not, matcher);
            return matchable.update(matcher);
        }   
    }
}
