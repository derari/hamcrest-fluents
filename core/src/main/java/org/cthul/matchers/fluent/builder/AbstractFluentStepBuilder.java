package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.object.InstanceOf;
import org.cthul.matchers.chain.*;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.fluent.FluentStep;
import org.cthul.matchers.fluent.adapters.AsTypeAdapter;
import org.cthul.matchers.fluent.ext.ExtensibleFluentStep;
import org.cthul.matchers.fluent.ext.FluentFactory;
import org.cthul.matchers.fluent.ext.StepFactory;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

/**
 * Base class for all fluent step implementations.
 * @param <Value> value type
 * @param <BaseFluent> second argument to {@link FluentStep}
 * @param <TheFluent> original fluent type
 * @param <This> this type
 */
public abstract class AbstractFluentStepBuilder
                <Value, BaseFluent, TheFluent extends BaseFluent,
                 This extends AbstractFluentStepBuilder<Value, BaseFluent, TheFluent, This>>
                implements ExtensibleFluentStep<Value, BaseFluent, TheFluent, This> {
    
    private boolean negate = false;
    private String prefix = null;
    
    public AbstractFluentStepBuilder() {
    }

    protected void _is() {
        prefix = "is";
    }
    
    protected void _has() {
        prefix = "has";
    }
    
    protected void _not() {
        negate = !negate;
    }
    
    protected String _prefix() {
        String p = prefix;
        prefix = null;
        return p;
    }
    
    protected boolean _negation() {
        boolean n = negate;
        negate = false;
        return n;
    }

    protected TheFluent _match(Matcher<? super Value> matcher) {
        return _apply(matcher, _prefix(), _negation());
    }
    
    protected TheFluent _match(Value value) {
        if (value instanceof Matcher) {
            return _match(new AmbiguousValueMatcher<>(value));
        }
        return _match(IsEqual.equalTo(value));
    }
    
    protected <P> TheFluent _match(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher) {
        return _match(adapter.adapt(matcher));
    }
    
    protected <P> FluentStep<P, TheFluent> _adapt(MatchValueAdapter<? super Value, ? extends P> adapter) {
        // equivalent, but creates a lot of wrapper overhead
//        return _adapt(FluentStepBuilder.<Value, P, TheFluent>adapter(adapter).asFactory());
        return _step(adapter, _prefix(), _negation());
    }
    
    protected <Step> Step _adapt(StepFactory<? super Value, TheFluent, Step> factory) {
        return _step(factory, _prefix(), _negation());
    }
    
    @SuppressWarnings("unchecked")
    protected This _this() {
        return (This) this;
    }
    
    protected <Chain> Chain _chain(Class<Chain> chainClass, StepFactory<? super Value, ?, ?> stepFactory, ChainFactory chainFactory, String completionToken) {
        StepFactory<? super Value, TheFluent, Chain> factory;
        factory = ExtensibleChainBuilder.factory(chainClass, stepFactory, chainFactory, completionToken);
        return _adapt(factory);
    }
    
    protected abstract TheFluent _apply(Matcher<? super Value> matcher, String prefix, boolean not);
    
    protected <Next> FluentStep<Next, TheFluent> _step(MatchValueAdapter<? super Value, ? extends Next> adapter, String prefix, boolean not) {
        return _step(_matchable(adapter, prefix, not));
    }
    
    protected <Step> Step _step(StepFactory<? super Value, TheFluent, Step> factory, String prefix, boolean not) {
        return factory.newStep(_matchable(prefix, not));
    }
    
    protected <Next, Fl> FluentStep<Next, Fl> _step(Matchable<? extends Next, Fl> matchable) {
        return new FluentStepBuilder<>(matchable);
    }
    
    protected Matchable<Value, TheFluent> _matchable(final String prefix, final boolean not) {
        return new Matchable<Value, TheFluent>() {
            @Override
            public TheFluent apply(Matcher<? super Value> matcher) {
                return _apply(matcher, prefix, not);
            }
            @Override
            public String toString() {
                return AbstractFluentStepBuilder.this.toString();
            }
        };
    }
    
    protected <Next> Matchable<Next, TheFluent> _matchable(final MatchValueAdapter<? super Value, Next> adapter, final String prefix, final boolean not) {
        return new Matchable<Next, TheFluent>() {
            @Override
            public TheFluent apply(Matcher<? super Next> matcher) {
                Matcher<? super Value> m = adapter.adapt(matcher);
                return _apply(m, prefix, not);
            }
            @Override
            public String toString() {
                return AbstractFluentStepBuilder.this.toString();
            }
        };
    }
    
    @Override
    public This is() {
        _is();
        return _this();
    }

    @Override
    public This has() {
        _has();
        return _this();
    }

    @Override
    public This not() {
        _not();
        return _this();
    }

    @Override
    public This isNot() {
        _is();
        _not();
        return _this();
    }

    @Override
    public This hasNot() {
        _has();
        _not();
        return _this();
    }

    @Override
    public TheFluent __(Matcher<? super Value> matcher) {
        return _match(matcher);
    }

    @Override
    public TheFluent is(Matcher<? super Value> matcher) {
        _is();
        return _match(matcher);
    }

    @Override
    public TheFluent has(Matcher<? super Value> matcher) {
        _has();
        return _match(matcher);
    }

    @Override
    public TheFluent not(Matcher<? super Value> matcher) {
        _not();
        return _match(matcher);
    }

    @Override
    public TheFluent isNot(Matcher<? super Value> matcher) {
        _is();
        _not();
        return _match(matcher);
    }

    @Override
    public TheFluent hasNot(Matcher<? super Value> matcher) {
        _has();
        _not();
        return _match(matcher);
    }

    @Override
    public TheFluent equalTo(Value value) {
        // don't use _match(value), this call is not ambiguous
        return _match(IsEqual.equalTo(value));
    }

    @Override
    public TheFluent is(Value value) {
        _is();
        return _match(value);
    }

    @Override
    public TheFluent not(Value value) {
        _not();
        return _match(value);
    }

    @Override
    public TheFluent isNot(Value value) {
        _is();
        _not();
        return _match(value);
    }

    @Override
    public TheFluent allOf(Matcher<? super Value>... matchers) {
        return _match(AndChainMatcher.all(matchers));
    }

    @Override
    public TheFluent anyOf(Matcher<? super Value>... matchers) {
        return _match(OrChainMatcher.any(matchers));
    }

    @Override
    public TheFluent noneOf(Matcher<? super Value>... matchers) {
        return _match(NOrChainMatcher.none(matchers));
//        _not();
//        return _match(OrChainMatcher.or(matcher));
    }

//    @Override
//    public TheFluent matches(int count, Matcher<? super Value>... matchers) {
//        return _match(SomeOfChainMatcher.matches(count, matchers));
//    }
//
//    @Override
//    public TheFluent matches(Matcher<? super Integer> countMatcher, Matcher<? super Value>... matchers) {
//        return _match(SomeOfChainMatcher.matches(countMatcher, matchers));
//    }
//
//    @Override
//    public TheFluent matches(ChainFactory chainType, Matcher<? super Value>... matchers) {
//        return _match(chainType.create(matchers));
//    }

    @Override
    public <NextValue> FluentStep<NextValue, TheFluent> __(MatchValueAdapter<? super Value, ? extends NextValue> adapter) {
        return _adapt(adapter);
    }
    
    @Override
    public <NextValue> FluentStep<NextValue, TheFluent> not(MatchValueAdapter<? super Value, ? extends NextValue> adapter) {
        _not();
        return _adapt(adapter);
    }
    
    @Override
    public <NextValue> FluentStep<NextValue, TheFluent> has(MatchValueAdapter<? super Value, ? extends NextValue> adapter) {
        _has();
        return _adapt(adapter);
    }
    
    @Override
    public <NextValue> FluentStep<NextValue, TheFluent> hasNot(MatchValueAdapter<? super Value, ? extends NextValue> adapter) {
        _has();
        _not();
        return _adapt(adapter);
    }

    @Override
    public <NextValue> TheFluent __(MatchValueAdapter<? super Value, ? extends NextValue> adapter, Matcher<? super NextValue> matcher) {
        return _match(adapter, matcher);
    }

    @Override
    public <NextValue> TheFluent has(MatchValueAdapter<? super Value, ? extends NextValue> adapter, Matcher<? super NextValue> matcher) {
        _has();
        return _match(adapter, matcher);
    }

    @Override
    public <NextValue> TheFluent not(MatchValueAdapter<? super Value, ? extends NextValue> adapter, Matcher<? super NextValue> matcher) {
        _not();
        return _match(adapter, matcher);
    }

    @Override
    public <NextValue> TheFluent hasNot(MatchValueAdapter<? super Value, ? extends NextValue> adapter, Matcher<? super NextValue> matcher) {
        _has();
        _not();
        return _match(adapter, matcher);
    }
    
//    @Override
//    public FluentStep.Both<Value, TheFluent> both(Matcher<? super Value> matcher) {
//        return new Both<>(this, matcher);
//    }
//    
//    protected static class Both<Property, TheFluent>
//                        implements FluentStep.Both<Property, TheFluent> {
//
//        private final AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property;
//        private final Matcher<? super Property> first;
//
//        public Both(AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Property> first) {
//            this.property = property;
//            this.first = first;
//        }
//        
//        @Override
//        public TheFluent and(Matcher<? super Property> matcher) {
//            return property.__(AndChainMatcher.both(first, matcher));
//        }
//    }
//
//    @Override
//    public FluentStep.Either<Value, TheFluent> either(Matcher<? super Value>... matchers) {
//        return new Either<>(this, matchers);
//    }
//    
//    protected static class ChainDsl<Property, TheFluent> {
//        
//        protected final AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property;
//        private final Matcher<? super Property>[] matchers;
//        
//        public ChainDsl(AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Property>... matchers) {
//            this.property = property;
//            this.matchers = Arrays.copyOf(matchers, matchers.length+1);
//        }
//        
//        protected void _add(Matcher<? super Property> matcher) {
//            matchers[matchers.length-1] = matcher;
//        }
//
//        protected Matcher<? super Property>[] _all() {
//            return matchers;
//        }
//    }
//
//    protected static class Either<Property, TheFluent>
//                        extends ChainDsl<Property, TheFluent>
//                        implements FluentStep.Either<Property, TheFluent> {
//
//        public Either(AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Property>... matchers) {
//            super(property, matchers);
//        }
//        
//        @Override
//        public TheFluent or(Matcher<? super Property> matcher) {
//            _add(matcher);
//            return property.__(OrChainMatcher.or(_all()));
//        }
//        
//        @Override
//        public TheFluent xor(Matcher<? super Property> matcher) {
//            _add(matcher);
//            return property.__(XOrChainMatcher.xor(_all()));
//        }
//    }
//    
//    @Override
//    public FluentStep.Neither<Value, TheFluent> neither(Matcher<? super Value>... matchers) {
//        return new Neither<>(this, matchers);
//    }
//
//    protected static class Neither<Property, TheFluent>
//                        extends ChainDsl<Property, TheFluent>
//                        implements FluentStep.Neither<Property, TheFluent> {
//
//        public Neither(AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Property>... matchers) {
//            super(property, matchers);
//        }
//        
//        @Override
//        public TheFluent nor(Matcher<? super Property> matcher) {
//            _add(matcher);
//            return property.__(NOrChainMatcher.nor(_all()));
//        }
//    }
//    
//    @Override
//    public FluentStep.MatchesSome<Value, TheFluent> matches(int count) {
//        return new MatchesSome<>(this, count);
//    }
//
//    @Override
//    public FluentStep.MatchesSome<Value, TheFluent> matches(Matcher<? super Integer> countMatcher) {
//        return new MatchesSome<>(this, countMatcher);
//    }
//
//    @Override
//    public FluentStep.MatchesSome<Value, TheFluent> matches(ChainFactory chainType) {
//        return new MatchesSome<>(this, chainType);
//    }
//    
//    protected static class MatchesSome<Property, TheFluent>
//                    implements FluentStep.MatchesSome<Property, TheFluent> {
//        
//        private final AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property;
//        private final ChainFactory chainType;
//
//        public MatchesSome(AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property, int count) {
//            this.property = property;
//            this.chainType = SomeOfChainMatcher.factory(count);
//        }
//        
//        public MatchesSome(AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Integer> countMatcher) {
//            this.property = property;
//            this.chainType = SomeOfChainMatcher.factory(countMatcher);
//        }
//        
//        public MatchesSome(AbstractFluentStepBuilder<Property, ? super TheFluent, TheFluent, ?> property, ChainFactory chainType) {
//            this.property = property;
//            this.chainType = chainType;
//        }
//
//        @Override
//        public TheFluent of(Matcher<? super Property>... matchers) {
//            return property.__(chainType.create(matchers));
//        }
//    }
    
    @Override
    public <Value2 extends Value> TheFluent isA(Class<Value2> clazz, Matcher<? super Value2> matcher) {
        return _match(InstanceOf.isA(clazz).that(matcher));
    }

    @Override
    public <Value2 extends Value> TheFluent hasType(Class<Value2> clazz) {
        return _match(InstanceOf.isA(clazz));
    }

    @Override
    public <Value2 extends Value> FluentStep<Value2, TheFluent> as(Class<Value2> clazz) {
        return _adapt(AsTypeAdapter.as(clazz));
    }

    @Override
    public <Step> Step as(StepFactory<? super Value, TheFluent, Step> adapter) {
        return _adapt(adapter);
    }
    
    protected <NextFluent> NextFluent asFluent(FluentFactory<? super Value, NextFluent> adapter) {
        if (_negation()) {
            throw new IllegalStateException(
                    "Negation not supported for fluent type cast");
        }
        return adapter.newFluent(_matchable(_prefix(), false));
    }

    @Override
    public AndChain<Value, ? extends TheFluent, ?> all() {
        return this._adapt(AbstractFluentStepBuilder.<Value, TheFluent>andChainFactory());
    }

    protected <Chain> Chain _both(Class<Chain> chainClass, StepFactory<? super Value, ?, ?> stepFactory) {
        return _chain(chainClass, stepFactory, AndChainMatcher.all(), "and");
    }
    
    @Override
    public AndChain<Value, ? extends TheFluent, ?> both() {
        return all();
    }

    @Override
    public AndChain<Value, ? extends TheFluent, ?> both(Matcher<? super Value> matcher) {
        return both().__(matcher);
    }
    
    protected <Chain> Chain _all(Class<Chain> chainClass, StepFactory<? super Value, ?, ?> stepFactory) {
        return _both(chainClass, stepFactory);
    }
    
    private static <Value, TheFluent> StepFactory<Value, TheFluent, AndChain<Value, ? extends TheFluent, ?>> andChainFactory() {
        return AND_CHAIN_FACTORY;
    }
    
    private static final StepFactory AND_CHAIN_FACTORY = new StepFactory() {
        @Override
        public Object newStep(Matchable matchable) {
            return new BasicAndChain(matchable, AndChainMatcher.all());
        }
    };
    
    protected static class BasicAndChain<Value, TheFluent> extends FluentChainBuilder<Value, TheFluent, BasicAndChain<Value, TheFluent>> implements AndChain<Value, TheFluent, BasicAndChain<Value, TheFluent>> {

        public BasicAndChain(Matchable<Value, TheFluent> matchable, ChainFactory chainFactory) {
            super(matchable, chainFactory);
        }

        @Override
        public FluentStep<Value, TheFluent> and() {
            return _terminate();
        }

        @Override
        public TheFluent and(Matcher<? super Value> matcher) {
            return and().__(matcher);
        }
    }
    
    protected <Chain> Chain _either(Class<Chain> chainClass, StepFactory<? super Value, ?, ?> stepFactory) {
        return _chain(chainClass, stepFactory, OrChainMatcher.any(), "or");
    }
    
    @Override
    public OrChain<Value, ? extends TheFluent, ?> either() {
        return this._adapt(AbstractFluentStepBuilder.<Value, TheFluent>orChainFactory());
    }

    @Override
    public OrChain<Value, ? extends TheFluent, ?> either(Matcher<? super Value> matcher) {
        return either().__(matcher);
    }
    
    private static <Value, TheFluent> StepFactory<Value, TheFluent, OrChain<Value, ? extends TheFluent, ?>> orChainFactory() {
        return OR_CHAIN_FACTORY;
    }
    
    private static final StepFactory OR_CHAIN_FACTORY = new StepFactory() {
        @Override
        public Object newStep(Matchable matchable) {
            return new BasicOrChain(matchable, OrChainMatcher.any());
        }
    };
    
    protected static class BasicOrChain<Value, TheFluent> extends FluentChainBuilder<Value, TheFluent, BasicOrChain<Value, TheFluent>> implements OrChain<Value, TheFluent, BasicOrChain<Value, TheFluent>> {

        public BasicOrChain(Matchable<Value, TheFluent> matchable, ChainFactory chainFactory) {
            super(matchable, chainFactory);
        }

        @Override
        public FluentStep<Value, TheFluent> or() {
            return _terminate();
        }

        @Override
        public TheFluent or(Matcher<? super Value> matcher) {
            return or().__(matcher);
        }
    }
    
    private static class AmbiguousValueMatcher<T> extends NestedResultMatcher<T> {
        private final Matcher<T> equalToMatcher;
        private final Matcher<T> matcherValue;
        
        public AmbiguousValueMatcher(T value) {
            this.equalToMatcher = IsEqual.equalTo(value);
            this.matcherValue = (Matcher) value;
        }

        @Override
        public void describeTo(Description description) {
            matcherValue.describeTo(description);
        }

        @Override
        public int getDescriptionPrecedence() {
            return Nested.precedenceOf(matcherValue);
        }
        
        @Override
        public boolean matches(Object o) {
            return equalToMatcher.matches(o) ||
                    matcherValue.matches(o);
        }

        @Override
        public <I> MatchResult<I> matchResult(I item) {
            if (item instanceof Matcher) {
                if (equalToMatcher.matches(item)) {
                    return QuickDiagnose.matchResult(equalToMatcher, item);
                }
                if (matcherValue.matches(item)) {
                    return QuickDiagnose.matchResult(equalToMatcher, item);
                }
                return QuickDiagnose.matchResult(equalToMatcher, item);
            }
            if (equalToMatcher.matches(item)) {
                return QuickDiagnose.matchResult(equalToMatcher, item);
            }
            return QuickDiagnose.matchResult(matcherValue, item);
        }
    }
}