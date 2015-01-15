package org.cthul.matchers.fluent.builder;

import java.lang.reflect.Method;
import org.cthul.matchers.object.InstanceOf;
import org.cthul.matchers.chain.*;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.fluent.FluentStep;
import org.cthul.matchers.fluent.adapters.AsTypeAdapter;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.ext.ExtensibleStepAdapter;
import org.cthul.matchers.fluent.ext.NewStep;
import org.cthul.matchers.fluent.ext.ExtensibleFluentStep;
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
    private Class<?> orChainClass = null;
    private Class<?> andChainClass = null;
    
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

    protected TheFluent _match(Matcher<? super Value> matcher) {
        String p = prefix;
        prefix = null;
        boolean n = negate;
        negate = false;
        return _apply(matcher, p, n);
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
        String p = prefix;
        prefix = null;
        boolean n = negate;
        negate = false;
        return _step(adapter, p, n);
    }
    
    protected <Next, Step> Step _adapt(ExtensibleStepAdapter<? super Value, Next, TheFluent, Step> adapter) {
        String p = prefix;
        prefix = null;
        boolean n = negate;
        negate = false;
        return _step(adapter, p, n);
    }
    
    @SuppressWarnings("unchecked")
    protected This _this() {
        return (This) this;
    }
    
    protected <Chain> Chain _chain(Class<Chain> chainClass, ChainFactory chainFactory, String completionToken) {
        String p = prefix;
        prefix = null;
        boolean n = negate;
        negate = false;
        Matchable<Value, TheFluent> m = _matchable(IdentityValue.<Value>value(), p, n);
        if (chainClass == FluentStep.OrChain.class) {
            return (Chain) new BasicOrChain<>(m, chainFactory);
        } else {
            return ExtensibleChainBuilder.create(chainClass, m, chainFactory, completionToken);
        }
    }
    
    protected <Chain> Chain _conjunction(Class<Chain> chainClass) {
        return _chain(chainClass, AndChainMatcher.all(), "and");
    }
    
    protected <Chain> Chain _disjunction(Class<Chain> chainClass) {
        return _chain(chainClass, OrChainMatcher.any(), "or");
    }
    
    protected abstract TheFluent _apply(Matcher<? super Value> matcher, String prefix, boolean not);
    
    protected <Next> FluentStep<Next, TheFluent> _step(MatchValueAdapter<? super Value, ? extends Next> adapter, String prefix, boolean not) {
        return _step(_matchable(adapter, prefix, not));
    }
    
    protected <Next, Step> Step _step(NewStep<Next, TheFluent, Step> factory, MatchValueAdapter<? super Value, Next> adapter, String prefix, boolean not) {
        return factory.create(_matchable(adapter, prefix, not));
    }
    
    protected <Next, Step> Step _step(ExtensibleStepAdapter<? super Value, Next, TheFluent, Step> adapter, String prefix, boolean not) {
        return _step(adapter.getStepFactory(), adapter.getAdapter(), prefix, not);
    }
    
    protected <Next, Fl> FluentStep<Next, Fl> _step(Matchable<? extends Next, Fl> matchable) {
        return new FluentStepBuilder<>(matchable);
    }
    
    protected <Next> Matchable<Next, TheFluent> _matchable(final MatchValueAdapter<? super Value, Next> adapter, final String prefix, final boolean not) {
        return new Matchable<Next, TheFluent>() {
            @Override
            public TheFluent apply(Matcher<? super Next> matcher) {
                Matcher<? super Value> m = adapter.adapt(matcher);
                return _apply(m, prefix, not);
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
        // don't use _match(value) this call is not ambiguous
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
    public <Value2, Step> Step as(ExtensibleStepAdapter<? super Value, Value2, TheFluent, Step> adapter) {
        return _adapt(adapter);
    }

    @Override
    public AndChain<Value, TheFluent, ?> all() {
        return _conjunction(AndChain.class);
    }

    @Override
    public AndChain<Value, TheFluent, ?> both() {
        return all();
    }

    @Override
    public AndChain<Value, TheFluent, ?> both(Matcher<? super Value> matcher) {
        return both().__(matcher);
    }
    
    protected Class<?> _orChainClass() {
        if (orChainClass == null) {
            orChainClass = _detectOrChainClass();
        }
        return orChainClass;
    }
    
    protected Class<?> _detectOrChainClass() {
        try {
            Method m = getClass().getMethod("either");
            return m.getReturnType();
        } catch (NoSuchMethodException e) {
            throw new AssertionError(
                    "Missing method: #either()");
        }
    }

    protected <C> C _disjunction() {
        return (C) _disjunction(_orChainClass());
    }
    
    @Override
    public OrChain<Value, ? extends TheFluent, ?> either() {
        return _disjunction();
    }

    @Override
    public OrChain<Value, ? extends TheFluent, ?> either(Matcher<? super Value> matcher) {
        return either().__(matcher);
    }
    
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