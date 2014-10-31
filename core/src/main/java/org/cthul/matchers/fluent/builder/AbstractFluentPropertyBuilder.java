package org.cthul.matchers.fluent.builder;

import java.util.Arrays;
import org.cthul.matchers.object.InstanceOf;
import org.cthul.matchers.chain.*;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.fluent.FluentProperty;
import org.cthul.matchers.fluent.adapters.AsTypeAdapter;
import org.cthul.matchers.fluent.ext.ExtendableFluentProperty;
import org.cthul.matchers.fluent.ext.ExtendedAdapter;
import org.cthul.matchers.fluent.ext.ExtendedAdapter.FluentPropertyFactory;
import org.cthul.matchers.fluent.ext.ExtendedAdapter.Matchable;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

/**
 * Base class for all fluent implementations.
 * @param <Property> property type
 * @param <TheFluent> type of fluent this property belongs to
 * @param <This> fluent interface implemented by this class
 */
public abstract class AbstractFluentPropertyBuilder
                <Property, BaseFluent, TheFluent extends BaseFluent,
                 This extends AbstractFluentPropertyBuilder<Property, BaseFluent, TheFluent, This>>
                implements ExtendableFluentProperty<Property, BaseFluent, This> {
    
    private boolean negate = false;
    private String prefix = null;
    private String reason = null;
    
    public AbstractFluentPropertyBuilder() {
    }

    protected String getReason() {
        return reason;
    }
    
    protected void _as(String reason) {
        this.reason = reason;
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

    protected TheFluent _match(Matcher<? super Property> matcher) {
        String p = prefix;
        prefix = null;
        boolean n = negate;
        negate = false;
        return _apply(matcher, p, n);
    }
    
    protected TheFluent _apply(Matcher<? super Property> matcher, String prefix, boolean not) {
        return _applyMatcher(matcher, prefix, not);
    }
    
    protected TheFluent _match(Property value) {
        if (value instanceof Matcher) {
            return _match(new AmbiguousValueMatcher<>(value));
        }
        return _match(IsEqual.equalTo(value));
    }
    
    protected <P> TheFluent _match(MatchValueAdapter<? super Property, P> adapter, Matcher<? super P> matcher) {
        return _match(adapter.adapt(matcher));
    }
    
    protected <P> FluentProperty<P, TheFluent> _adapt(MatchValueAdapter<? super Property, ? extends P> adapter) {
        String p = prefix;
        prefix = null;
        boolean n = negate;
        negate = false;
        return _newProperty(adapter, p, n);
    }
    
    @SuppressWarnings("unchecked")
    protected This _this() {
        return (This) this;
    }
    
    protected abstract TheFluent _applyMatcher(Matcher<? super Property> matcher, String prefix, boolean not);
    
    protected <P> FluentProperty<P, TheFluent> _newProperty(MatchValueAdapter<? super Property, ? extends P> adapter, String prefix, boolean not) {
        return _newProperty(matchable(adapter, prefix, not));
    }
    
    protected <P, FlP> FlP _newProperty(FluentPropertyFactory<P, TheFluent, FlP> factory, MatchValueAdapter<? super Property, P> adapter, String prefix, boolean not) {
        return factory.create(matchable(adapter, prefix, not));
    }
    
    protected <P, FlP> FlP _newProperty(ExtendedAdapter<Property, P, TheFluent, FlP> adapter, String prefix, boolean not) {
        return _newProperty(adapter.getFactory(), adapter.getAdapter(), prefix, not);
    }
    
    protected <P> FluentProperty<P, TheFluent> _newProperty(Matchable<? extends P, TheFluent> matchable) {
        return new FluentPropertyBuilder<>(matchable);
    }
    
    protected <P> Matchable<P, TheFluent> matchable(final MatchValueAdapter<? super Property, P> adapter, final String prefix, final boolean not) {
        return new ExtendedAdapter.Matchable<P, TheFluent>() {
            @Override
            public TheFluent apply(Matcher<? super P> matcher) {
                Matcher<? super Property> m = adapter.adapt(matcher);
                return _apply(m, prefix, not);
            }
        };
    }
    
//    @Override
//    public This as(String reason) {
//        _as(reason);
//        return _this();
//    }
//
//    @Override
//    public This as(String reason, Object... args) {
//        _as(String.format(reason, args));
//        return _this();
//    }

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
    public TheFluent __(Matcher<? super Property> matcher) {
        return _match(matcher);
    }

    @Override
    public TheFluent is(Matcher<? super Property> matcher) {
        _is();
        return _match(matcher);
    }

    @Override
    public TheFluent has(Matcher<? super Property> matcher) {
        _has();
        return _match(matcher);
    }

    @Override
    public TheFluent not(Matcher<? super Property> matcher) {
        _not();
        return _match(matcher);
    }

    @Override
    public TheFluent isNot(Matcher<? super Property> matcher) {
        _is();
        _not();
        return _match(matcher);
    }

    @Override
    public TheFluent hasNot(Matcher<? super Property> matcher) {
        _has();
        _not();
        return _match(matcher);
    }

    @Override
    public TheFluent equalTo(Property value) {
        // don't use _match(value) this call is not ambiguous
        return _match(IsEqual.equalTo(value));
    }

    @Override
    public TheFluent is(Property value) {
        _is();
        return _match(value);
    }

    @Override
    public TheFluent not(Property value) {
        _not();
        return _match(value);
    }

    @Override
    public TheFluent isNot(Property value) {
        _is();
        _not();
        return _match(value);
    }

    @Override
    public TheFluent all(Matcher<? super Property>... matchers) {
        return _match(AndChainMatcher.all(matchers));
    }

    @Override
    public TheFluent any(Matcher<? super Property>... matchers) {
        return _match(OrChainMatcher.any(matchers));
    }

    @Override
    public TheFluent none(Matcher<? super Property>... matchers) {
        return _match(NOrChainMatcher.none(matchers));
//        _not();
//        return _match(OrChainMatcher.or(matcher));
    }

    @Override
    public TheFluent matches(int count, Matcher<? super Property>... matchers) {
        return _match(SomeOfChainMatcher.matches(count, matchers));
    }

    @Override
    public TheFluent matches(Matcher<? super Integer> countMatcher, Matcher<? super Property>... matchers) {
        return _match(SomeOfChainMatcher.matches(countMatcher, matchers));
    }

    @Override
    public TheFluent matches(ChainFactory chainType, Matcher<? super Property>... matchers) {
        return _match(chainType.create(matchers));
    }

    @Override
    public <NextProperty> FluentProperty<NextProperty, TheFluent> __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        return _adapt(adapter);
    }
    
    @Override
    public <NextProperty> FluentProperty<NextProperty, TheFluent> not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        _not();
        return _adapt(adapter);
    }
    
    @Override
    public <NextProperty> FluentProperty<NextProperty, TheFluent> has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        _has();
        return _adapt(adapter);
    }
    
    @Override
    public <NextProperty> FluentProperty<NextProperty, TheFluent> hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        _has();
        _not();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> TheFluent __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> TheFluent has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _has();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> TheFluent not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _not();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> TheFluent hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _has();
        _not();
        return _match(adapter, matcher);
    }
    
    @Override
    public FluentProperty.Both<Property, TheFluent> both(Matcher<? super Property> matcher) {
        return new Both<>(this, matcher);
    }
    
    protected static class Both<Property, TheFluent>
                        implements FluentProperty.Both<Property, TheFluent> {

        private final AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property;
        private final Matcher<? super Property> first;

        public Both(AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Property> first) {
            this.property = property;
            this.first = first;
        }
        
        @Override
        public TheFluent and(Matcher<? super Property> matcher) {
            return property.__(AndChainMatcher.both(first, matcher));
        }
    }

    @Override
    public FluentProperty.Either<Property, TheFluent> either(Matcher<? super Property>... matchers) {
        return new Either<>(this, matchers);
    }
    
    protected static class ChainDsl<Property, TheFluent> {
        
        protected final AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property;
        private final Matcher<? super Property>[] matchers;
        
        public ChainDsl(AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Property>... matchers) {
            this.property = property;
            this.matchers = Arrays.copyOf(matchers, matchers.length+1);
        }
        
        protected void _add(Matcher<? super Property> matcher) {
            matchers[matchers.length-1] = matcher;
        }

        protected Matcher<? super Property>[] _all() {
            return matchers;
        }
    }

    protected static class Either<Property, TheFluent>
                        extends ChainDsl<Property, TheFluent>
                        implements FluentProperty.Either<Property, TheFluent> {

        public Either(AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Property>... matchers) {
            super(property, matchers);
        }
        
        @Override
        public TheFluent or(Matcher<? super Property> matcher) {
            _add(matcher);
            return property.__(OrChainMatcher.or(_all()));
        }
        
        @Override
        public TheFluent xor(Matcher<? super Property> matcher) {
            _add(matcher);
            return property.__(XOrChainMatcher.xor(_all()));
        }
    }
    
    @Override
    public FluentProperty.Neither<Property, TheFluent> neither(Matcher<? super Property>... matchers) {
        return new Neither<>(this, matchers);
    }

    protected static class Neither<Property, TheFluent>
                        extends ChainDsl<Property, TheFluent>
                        implements FluentProperty.Neither<Property, TheFluent> {

        public Neither(AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Property>... matchers) {
            super(property, matchers);
        }
        
        @Override
        public TheFluent nor(Matcher<? super Property> matcher) {
            _add(matcher);
            return property.__(NOrChainMatcher.nor(_all()));
        }
    }
    
    @Override
    public FluentProperty.MatchesSome<Property, TheFluent> matches(int count) {
        return new MatchesSome<>(this, count);
    }

    @Override
    public FluentProperty.MatchesSome<Property, TheFluent> matches(Matcher<? super Integer> countMatcher) {
        return new MatchesSome<>(this, countMatcher);
    }

    @Override
    public FluentProperty.MatchesSome<Property, TheFluent> matches(ChainFactory chainType) {
        return new MatchesSome<>(this, chainType);
    }
    
    protected static class MatchesSome<Property, TheFluent>
                    implements FluentProperty.MatchesSome<Property, TheFluent> {
        
        private final AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property;
        private final ChainFactory chainType;

        public MatchesSome(AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property, int count) {
            this.property = property;
            this.chainType = SomeOfChainMatcher.factory(count);
        }
        
        public MatchesSome(AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property, Matcher<? super Integer> countMatcher) {
            this.property = property;
            this.chainType = SomeOfChainMatcher.factory(countMatcher);
        }
        
        public MatchesSome(AbstractFluentPropertyBuilder<Property, ? super TheFluent, TheFluent, ?> property, ChainFactory chainType) {
            this.property = property;
            this.chainType = chainType;
        }

        @Override
        public TheFluent of(Matcher<? super Property>... matchers) {
            return property.__(chainType.create(matchers));
        }
    }
    
    @Override
    public <Property2 extends Property> TheFluent isA(Class<Property2> clazz, Matcher<? super Property2> matcher) {
        return _match(InstanceOf.isA(clazz).that(matcher));
    }

    @Override
    public <Property2 extends Property> TheFluent isA(Class<Property2> clazz) {
        return _match(InstanceOf.isA(clazz));
    }

    protected <Property2 extends Property> FluentProperty<Property2, TheFluent> _as(Class<Property2> clazz) {
        return _adapt(AsTypeAdapter.as(clazz));
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