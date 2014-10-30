package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.FluentPropertyMatcher;
import org.cthul.matchers.fluent.ext.ExtendableFluentPropertyMatcher;
import org.cthul.matchers.fluent.ext.ExtendedAdapter.Matchable;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.cthul.matchers.object.CIs;
import org.hamcrest.Matcher;

/**
 * Base class for all fluent matcher implementations
 * @param <Value> base value type
 * @param <Property> property type
 * @param <Match> match value type
 * @param <ThisFluent> type of fluent this property belongs to
 * @param <This> fluent interface implemented by this class
 */
public abstract class AbstractPropertyMatcherBuilder
                <Value, Property, Match, ThisFluent extends FluentMatcher<Value, Match>,
                 This extends AbstractPropertyMatcherBuilder<Value, Property, Match, ThisFluent, This>>
                extends AbstractFluentPropertyBuilder<Value, Property, ThisFluent, This>
                implements FluentPropertyMatcher<Value, Property, Match>,
                ExtendableFluentPropertyMatcher<Value, Property, Match, ThisFluent, This> {

    @Override
    protected <P> FluentPropertyMatcher<Value, P, Match> _newProperty(Matchable<P, ThisFluent> matchable) {
        return new MatcherPropertyBuilder<>(matchable);
    }

    @Override
    public <NextProperty> FluentPropertyMatcher<Value, NextProperty, Match> __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        return (FluentPropertyMatcher) super.__(adapter);
    }
    
    @Override
    public <NextProperty> FluentPropertyMatcher<Value, NextProperty, Match> has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        return (FluentPropertyMatcher) super.has(adapter);
    }
    
    @Override
    public <NextProperty> FluentPropertyMatcher<Value, NextProperty, Match> not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        return (FluentPropertyMatcher) super.not(adapter);
    }
    
    @Override
    public <NextProperty> FluentPropertyMatcher<Value, NextProperty, Match> hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        return (FluentPropertyMatcher) super.hasNot(adapter);
    }
    
    @Override
    public Both<Value, Property, Match, ThisFluent> both(Matcher<? super Property> matcher) {
        return new Both<>(this, matcher);
    }
    
    protected static class Both<Value, Property, Match,
                                ThisFluent extends FluentMatcher<Value, Match>>
                        extends AbstractFluentPropertyBuilder.Both<Value, Property, ThisFluent>
                        implements FluentPropertyMatcher.Both<Value, Property, Match> {

        public Both(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property> first) {
            super(property, first);
        }
    }
    
    @Override
    public Either<Value, Property, Match, ThisFluent> either(Matcher<? super Property>... matchers) {
        return new Either<>(this, matchers);
    }
    
    protected static class Either<Value, Property, Match,
                                ThisFluent extends FluentMatcher<Value, Match>>
                        extends AbstractFluentPropertyBuilder.Either<Value, Property, ThisFluent>
                        implements FluentPropertyMatcher.Either<Value, Property, Match> {

        public Either(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property>... matchers) {
            super(property, matchers);
        }
    }
    
    @Override
    public Neither<Value, Property, Match, ThisFluent> neither(Matcher<? super Property>... matchers) {
        return new Neither<>(this, matchers);
    }
    
    protected static class Neither<Value, Property, Match,
                                ThisFluent extends FluentMatcher<Value, Match>>
                        extends AbstractFluentPropertyBuilder.Neither<Value, Property, ThisFluent>
                        implements FluentPropertyMatcher.Neither<Value, Property, Match> {

        public Neither(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property>... matchers) {
            super(property, matchers);
        }
    }
    
    @Override
    public MatchesSome<Value, Property, Match, ThisFluent> matches(int count) {
        return new MatchesSome<>(this, count);
    }

    @Override
    public MatchesSome<Value, Property, Match, ThisFluent> matches(Matcher<? super Integer> countMatcher) {
        return new MatchesSome<>(this, countMatcher);
    }

    @Override
    public MatchesSome<Value, Property, Match, ThisFluent> matches(ChainFactory chainType) {
        return new MatchesSome<>(this, chainType);
    }
    
    protected static class MatchesSome<Value, Property, Match,
                                ThisFluent extends FluentMatcher<Value, Match>>
                        extends AbstractFluentPropertyBuilder.MatchesSome<Value, Property, ThisFluent>
                        implements FluentPropertyMatcher.MatchesSome<Value, Property, Match> {

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
    public <Property2 extends Property> FluentPropertyMatcher.IsA<Value, Property2, Match> isA(Class<Property2> clazz) {
        return (FluentPropertyMatcher.IsA) super.isA(clazz);
    }

    @Override
    protected Class<?> getIsAInterface() {
        return FluentPropertyMatcher.IsA.class;
    }
    
    protected static class MatcherPropertyBuilder
                    <Value, Property, Match,
                     ThisFluent extends FluentMatcher<Value, Match>,
                     This extends MatcherPropertyBuilder<Value, Property, Match, ThisFluent, This>>
                    extends AbstractPropertyMatcherBuilder<Value, Property, Match, ThisFluent, This> 
                    implements FluentPropertyMatcher<Value, Property, Match> {
        
        private final Matchable<Property, ThisFluent> matchable;

        public MatcherPropertyBuilder(Matchable<Property, ThisFluent> matchable) {
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
//    
//    protected class MatcherPropertyBuilder
//                    <Property2, This2 extends MatcherPropertyBuilder<Property2, This2>>
//                    extends AbstractPropertyMatcherBuilder<Value, Property2, Match, ThisFluent, This2> 
//                    implements FluentPropertyMatcher<Value, Property2, Match> {
//        
//        private final MatchValueAdapter<? super Property, Property2> adapter;
//        private final String flPrefix;
//        private final boolean flNot;
//
//        public MatcherPropertyBuilder(MatchValueAdapter<? super Property, Property2> adapter, String prefix, boolean not) {
//            this.adapter = adapter;
//            this.flPrefix = prefix;
//            this.flNot = not;
//        }
//
//        @Override
//        protected ThisFluent _applyMatcher(Matcher<? super Property2> matcher, String prefix, boolean not) {
//            Matcher<? super Property> m = adapter.adapt(CIs.wrap(prefix, not, matcher));
//            return AbstractPropertyMatcherBuilder.this._apply(m, flPrefix, flNot);
//        }
//
//        @Override
//        protected ThisFluent _updateMatcher(Matcher<? super Property2> matcher, String prefix, boolean not) {
//            Matcher<? super Property> m = adapter.adapt(CIs.wrap(prefix, not, matcher));
//            return AbstractPropertyMatcherBuilder.this._update(m, flPrefix, flNot);
//        }
//    }
}
