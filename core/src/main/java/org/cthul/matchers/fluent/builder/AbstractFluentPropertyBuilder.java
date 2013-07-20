package org.cthul.matchers.fluent.builder;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.cthul.matchers.InstanceOf;
import org.cthul.matchers.chain.AndChainMatcher;
import org.cthul.matchers.chain.NOrChainMatcher;
import org.cthul.matchers.chain.OrChainMatcher;
import org.cthul.matchers.chain.XOrChainMatcher;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentProperty;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 *
 */
public abstract class AbstractFluentPropertyBuilder
                <Value, Property, ThisFluent extends Fluent<Value>,
                 This extends FluentProperty<Value, Property>>
                implements FluentProperty<Value, Property> {
    
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

    protected ThisFluent _match(Matcher<? super Property> matcher) {
        String p = prefix;
        prefix = null;
        boolean n = negate;
        negate = false;
        return _applyMatcher(matcher, p, n);
    }
    
    protected abstract ThisFluent _applyMatcher(Matcher<? super Property> matcher, String prefix, boolean not);
    
    @SuppressWarnings("unchecked")
    protected This _this() {
        return (This) this;
    }
    
    protected abstract <P> FluentProperty<Value, P> _newProperty(MatchValueAdapter<? super Property, P> adapter, String prefix, boolean not);
    
    @Override
    public This as(String reason) {
        _as(reason);
        return _this();
    }

    @Override
    public This as(String reason, Object... args) {
        _as(String.format(reason, args));
        return _this();
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
    public ThisFluent _(Matcher<? super Property> matcher) {
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
    public ThisFluent all(Matcher<? super Property>... matcher) {
        return _match(AndChainMatcher.all(matcher));
    }

    @Override
    public ThisFluent any(Matcher<? super Property>... matcher) {
        return _match(OrChainMatcher.or(matcher));
    }

    @Override
    public ThisFluent none(Matcher<? super Property>... matcher) {
        _not();
        return _match(OrChainMatcher.or(matcher));
    }

    @Override
    public <P> FluentProperty<Value, P> _(MatchValueAdapter<? super Property, P> adapter) {
        return _newProperty(adapter, null, false);
    }
    
    @Override
    public <P> FluentProperty<Value, P> not(MatchValueAdapter<? super Property, P> adapter) {
        _not();
        return _newProperty(adapter, null, false);
    }
    
    @Override
    public <P> FluentProperty<Value, P> has(MatchValueAdapter<? super Property, P> adapter) {
        _has();
        return _newProperty(adapter, null, false);
    }
    
    @Override
    public <P> FluentProperty<Value, P> hasNot(MatchValueAdapter<? super Property, P> adapter) {
        _has();
        _not();
        return _newProperty(adapter, null, false);
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
            return property._(AndChainMatcher.both(first, matcher));
        }
    }

    @Override
    public abstract FluentProperty.Either<Value, Property> either(Matcher<? super Property>... matchers);

    protected static class Either<Value, Property, 
                                ThisFluent extends Fluent<Value>>
                        implements FluentProperty.Either<Value, Property> {

        private final AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property;
        private final Matcher<? super Property>[] matchers;

        public Either(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property>... matchers) {
            this.property = property;
            this.matchers = Arrays.copyOf(matchers, matchers.length+1);
        }
        
        @Override
        public ThisFluent or(Matcher<? super Property> matcher) {
            matchers[matchers.length-1] = matcher;
            return property._(OrChainMatcher.or(matchers));
        }
        
        @Override
        public ThisFluent xor(Matcher<? super Property> matcher) {
            matchers[matchers.length-1] = matcher;
            return property._(XOrChainMatcher.xor(matchers));
        }
    }
    
    @Override
    public abstract FluentProperty.Neither<Value, Property> neither(Matcher<? super Property>... matchers);

    protected static class Neither<Value, Property, 
                                ThisFluent extends Fluent<Value>>
                        implements FluentProperty.Neither<Value, Property> {

        private final AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property;
        private final Matcher<? super Property>[] matchers;

        public Neither(AbstractFluentPropertyBuilder<Value, Property, ThisFluent, ?> property, Matcher<? super Property>... matchers) {
            this.property = property;
            this.matchers = Arrays.copyOf(matchers, matchers.length+1);
        }
        
        @Override
        public ThisFluent nor(Matcher<? super Property> matcher) {
            matchers[matchers.length-1] = matcher;
            return property._(NOrChainMatcher.nor(matchers));
        }
    }
    
    @Override
    public <Property2 extends Property> Fluent<? extends Value> isA(Class<Property2> clazz, Matcher<? super Property2> matcher) {
        _is();
        return _match(InstanceOf.isA(clazz).that(matcher));
    }

    @Override
    public <Property2 extends Property> FluentProperty.IsA<? extends Value, Property2> isA(Class<Property2> clazz) {
        ThisFluent fluent = _match(InstanceOf.isA(clazz));
        IsAImpl<Property> isA = new IsAImpl<>(this);
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
        
        private final FluentProperty<?, Property> property;

        public IsAImpl(FluentProperty<?, Property> property) {
            this.property = property;
        }

        public FluentProperty<?, Property> that() {
            return property;
        }

        public FluentProperty<?, Property> thatIs() {
            return property.is();
        }

        public Fluent<?> that(Matcher<? super Property> matcher) {
            return property._(matcher);
        }
        
        public Fluent<?> thatIs(Matcher<? super Property> matcher) {
            return property.is(matcher);
        }
    }
}
