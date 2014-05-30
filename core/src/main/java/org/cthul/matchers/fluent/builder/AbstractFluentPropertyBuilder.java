package org.cthul.matchers.fluent.builder;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.cthul.matchers.object.CIs;
import org.cthul.matchers.object.InstanceOf;
import org.cthul.matchers.chain.*;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcherBase;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentProperty;
import org.cthul.matchers.fluent.intern.SwitchInvocationHandler;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

/**
 * Base class for all fluent implementations.
 * @param <Value> base value type
 * @param <Property> property type
 * @param <ThisFluent> type of fluent this property belongs to
 * @param <This> fluent interface implemented by this class
 */
public abstract class AbstractFluentPropertyBuilder
                <Value, Property, ThisFluent extends Fluent<Value>,
                 This extends FluentProperty<Value, Property>>
                implements FluentProperty<Value, Property> {
    
    private boolean negate = false;
    private String prefix = null;
    private String reason = null;
    
    private IsAMatcher isAMatcher = null;
    
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

    protected ThisFluent _match(Matcher<? super Property> matcher) {
        String p = prefix;
        prefix = null;
        boolean n = negate;
        negate = false;
        return _apply(matcher, p, n);
    }
    
    protected ThisFluent _apply(Matcher<? super Property> matcher, String prefix, boolean not) {
        if (isAMatcher == null) {
            return _applyMatcher(matcher, prefix, not);
        } else {
            Matcher<Object> m = isAMatcher.that(matcher, prefix, not);
            isAMatcher = null;
            return _updateMatcher(m, null, false);
        }
    }
    
    protected ThisFluent _update(Matcher<? super Property> matcher, String prefix, boolean not) {
        return _updateMatcher(matcher, prefix, not);
    }
    
    protected ThisFluent _match(Property value) {
        return _match(IsEqual.equalTo(value));
    }
    
    protected <P> ThisFluent _match(MatchValueAdapter<? super Property, P> adapter, Matcher<? super P> matcher) {
        return _match(adapter.adapt(matcher));
    }
    
    protected <FP> FP _adapt(MatchValueAdapter<? super Property, ?> adapter) {
        String p = prefix;
        prefix = null;
        boolean n = negate;
        negate = false;
        return (FP) _newProperty(adapter, p, n);
    }
    
    @SuppressWarnings("unchecked")
    protected This _this() {
        return (This) this;
    }
    
    protected abstract ThisFluent _applyMatcher(Matcher<? super Property> matcher, String prefix, boolean not);
    
    protected abstract ThisFluent _updateMatcher(Matcher<? super Property> matcher, String prefix, boolean not);
    
    protected abstract <P> FluentProperty<Value, P> _newProperty(MatchValueAdapter<? super Property, P> adapter, String prefix, boolean not);
    
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
    public ThisFluent __(Matcher<? super Property> matcher) {
        return _match(matcher);
    }

    @Override
    public ThisFluent is(Matcher<? super Property> matcher) {
        _is();
        return _match(matcher);
    }

    @Override
    public ThisFluent has(Matcher<? super Property> matcher) {
        _has();
        return _match(matcher);
    }

    @Override
    public ThisFluent not(Matcher<? super Property> matcher) {
        _not();
        return _match(matcher);
    }

    @Override
    public ThisFluent isNot(Matcher<? super Property> matcher) {
        _is();
        _not();
        return _match(matcher);
    }

    @Override
    public ThisFluent hasNot(Matcher<? super Property> matcher) {
        _has();
        _not();
        return _match(matcher);
    }

    @Override
    public ThisFluent equalTo(Property value) {
        return _match(value);
    }

    @Override
    public ThisFluent is(Property value) {
        _is();
        return _match(value);
    }

    @Override
    public ThisFluent not(Property value) {
        _not();
        return _match(value);
    }

    @Override
    public ThisFluent isNot(Property value) {
        _is();
        _not();
        return _match(value);
    }

    @Override
    public ThisFluent all(Matcher<? super Property>... matchers) {
        return _match(AndChainMatcher.all(matchers));
    }

    @Override
    public ThisFluent any(Matcher<? super Property>... matchers) {
        return _match(OrChainMatcher.any(matchers));
    }

    @Override
    public ThisFluent none(Matcher<? super Property>... matchers) {
        return _match(NOrChainMatcher.none(matchers));
//        _not();
//        return _match(OrChainMatcher.or(matcher));
    }

    @Override
    public ThisFluent matches(int count, Matcher<? super Property>... matchers) {
        return _match(SomeOfChainMatcher.matches(count, matchers));
    }

    @Override
    public ThisFluent matches(Matcher<? super Integer> countMatcher, Matcher<? super Property>... matchers) {
        return _match(SomeOfChainMatcher.matches(countMatcher, matchers));
    }

    @Override
    public ThisFluent matches(ChainFactory chainType, Matcher<? super Property>... matchers) {
        return _match(chainType.create(matchers));
    }

    @Override
    public <NextProperty> FluentProperty<Value, NextProperty> __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        return _adapt(adapter);
    }
    
    @Override
    public <NextProperty> FluentProperty<Value, NextProperty> not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        _not();
        return _adapt(adapter);
    }
    
    @Override
    public <NextProperty> FluentProperty<Value, NextProperty> has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        _has();
        return _adapt(adapter);
    }
    
    @Override
    public <NextProperty> FluentProperty<Value, NextProperty> hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter) {
        _has();
        _not();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> ThisFluent __(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> ThisFluent has(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _has();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> ThisFluent not(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _not();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> ThisFluent hasNot(MatchValueAdapter<? super Property, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _has();
        _not();
        return _match(adapter, matcher);
    }
    
    @Override
    public abstract FluentProperty.Both<Value, Property> both(Matcher<? super Property> matcher);
    
    protected static class Both<Value, Property, 
                                ThisFluent extends Fluent<Value>>
                        implements FluentProperty.Both<Value, Property> {

        private final AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property;
        private final Matcher<? super Property> first;

        public Both(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property> first) {
            this.property = property;
            this.first = first;
        }
        
        @Override
        public ThisFluent and(Matcher<? super Property> matcher) {
            return property.__(AndChainMatcher.both(first, matcher));
        }
    }

    @Override
    public abstract FluentProperty.Either<Value, Property> either(Matcher<? super Property>... matchers);
    
    protected static class ChainDsl<Value, Property, 
                                ThisFluent extends Fluent<Value>> {
        
        protected final AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property;
        private final Matcher<? super Property>[] matchers;
        
        public ChainDsl(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property>... matchers) {
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

    protected static class Either<Value, Property, 
                                ThisFluent extends Fluent<Value>>
                        extends ChainDsl<Value, Property, ThisFluent>
                        implements FluentProperty.Either<Value, Property> {

        public Either(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property>... matchers) {
            super(property, matchers);
        }
        
        @Override
        public ThisFluent or(Matcher<? super Property> matcher) {
            _add(matcher);
            return property.__(OrChainMatcher.or(_all()));
        }
        
        @Override
        public ThisFluent xor(Matcher<? super Property> matcher) {
            _add(matcher);
            return property.__(XOrChainMatcher.xor(_all()));
        }
    }
    
    @Override
    public abstract FluentProperty.Neither<Value, Property> neither(Matcher<? super Property>... matchers);

    protected static class Neither<Value, Property, 
                                ThisFluent extends Fluent<Value>>
                        extends ChainDsl<Value, Property, ThisFluent>
                        implements FluentProperty.Neither<Value, Property> {

        public Neither(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property>... matchers) {
            super(property, matchers);
        }
        
        @Override
        public ThisFluent nor(Matcher<? super Property> matcher) {
            _add(matcher);
            return property.__(NOrChainMatcher.nor(_all()));
        }
    }
    
    @Override
    public abstract FluentProperty.MatchesSome<Value, Property> matches(int count);

    @Override
    public abstract FluentProperty.MatchesSome<Value, Property> matches(Matcher<? super Integer> countMatcher);

    @Override
    public abstract FluentProperty.MatchesSome<Value, Property> matches(ChainFactory chainType);
    
    protected static class MatchesSome<Value, Property,
                            ThisFluent extends Fluent<Value>>
                    implements FluentProperty.MatchesSome<Value, Property> {
        
        private final AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property;
        private final ChainFactory chainType;

        public MatchesSome(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, int count) {
            this.property = property;
            this.chainType = SomeOfChainMatcher.factory(count);
        }
        
        public MatchesSome(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Integer> countMatcher) {
            this.property = property;
            this.chainType = SomeOfChainMatcher.factory(countMatcher);
        }
        
        public MatchesSome(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, ChainFactory chainType) {
            this.property = property;
            this.chainType = chainType;
        }

        @Override
        public ThisFluent of(Matcher<? super Property>... matchers) {
            return property.__(chainType.create(matchers));
        }
    }
    
    @Override
    public <Property2 extends Property> Fluent<? extends Value> isA(Class<Property2> clazz, Matcher<? super Property2> matcher) {
        return _match(InstanceOf.isA(clazz).that(matcher));
    }

    @Override
    public <Property2 extends Property> FluentProperty.IsA<? extends Value, Property2> isA(Class<Property2> clazz) {
        IsAImpl<Property> isA = new IsAImpl<>(clazz, this);
        ThisFluent fluent = _match(isA.getInternalMatcher());
        Class[] actualIface = { getIsAInterface() };
        return (FluentProperty.IsA) Proxy.newProxyInstance(
                IsAImpl.class.getClassLoader(), actualIface,
                new SwitchInvocationHandler(fluent, isA));
    }
    
    protected Class<?> getIsAInterface() {
        try {
            return (Class) getClass().getMethod("isA", Class.class).getReturnType();
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected static class IsAImpl<Property> {
        
        private final IsAMatcher isAMatcher;
        private final AbstractFluentPropertyBuilder<?, Property, ?, ?> property;

        public IsAImpl(Class<?> clazz, AbstractFluentPropertyBuilder<?, Property, ?, ?> property) {
            this.isAMatcher = new IsAMatcher<>(clazz);
            this.property = property;
        }
        
        public <T> IsAMatcher<T> getInternalMatcher() {
            return isAMatcher;
        }

        public FluentProperty<?, Property> that() {
            property.isAMatcher = isAMatcher;
            return property;
        }

        public FluentProperty<?, Property> thatIs() {
            return that().is();
        }

        public Fluent<?> that(Matcher<? super Property> matcher) {
            return that().__(matcher);
        }
        
        public Fluent<?> thatIs(Matcher<? super Property> matcher) {
            return that().is(matcher);
        }
    }
    
    /**
     * Used by {@link AbstractFluentPropertyBuilder#isA(java.lang.Class)}.
     */
    protected static class IsAMatcher<T> extends QuickDiagnosingMatcherBase<Object> {

        private final InstanceOf<T> isA;
        Matcher<Object> isA_that = null;

        public IsAMatcher(Class<T> expectedType) {
            isA = InstanceOf.isA(expectedType);
        }
        
        public Matcher<Object> that(Matcher<T> nested, String prefix, boolean not) {
            nested = CIs.wrap(prefix, not, nested);
            this.isA_that = isA.that(nested);
            return this;
        }

        @Override
        public boolean matches(Object item, Description mismatch) {
            if (isA_that != null) {
                return quickMatch(isA_that, item, mismatch);
            } else {
                return quickMatch(isA, item, mismatch);
            }
        }

        @Override
        public boolean matches(Object item) {
            if (isA_that != null) {
                return isA_that.matches(item);
            } else {
                return isA.matches(item);
            }
        }

        @Override
        public void describeMismatch(Object item, Description mismatch) {
            if (isA_that != null) {
                isA_that.describeMismatch(item, mismatch);
            } else {
                isA.describeMismatch(item, mismatch);
            }
        }

        @Override
        public void describeTo(Description description) {
            if (isA_that != null) {
                isA_that.describeTo(description);
            } else {
                description.appendText("is ");
                isA.describeTo(description);
            }
        }       
    }
}