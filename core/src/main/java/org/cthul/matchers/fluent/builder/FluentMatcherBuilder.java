package org.cthul.matchers.fluent.builder;

import java.util.ArrayList;
import java.util.List;
import org.cthul.matchers.object.CIs;
import org.cthul.matchers.chain.*;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.cthul.matchers.diagnose.nested.MatcherDescription;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.fluent.Fluent;
import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.FluentProperty;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.ext.ExtendableFluentMatcher;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.*;

/**
 * Implements {@link FluentMatcher}.
 * @param <Value> base value type
 * @param <Match> match value type
 * @param <This> fluent interface implemented by this class
 */
public class FluentMatcherBuilder
                <Value, Match, This extends FluentMatcherBuilder<Value, Match, This>> 
                extends AbstractFluentBuilder<Value, This>
                implements ExtendableFluentMatcher<Value, Match, This> {
    
    @Factory
    public static <V> FluentMatcher<V, V> match() {
        return new FluentMatcherBuilder<>(IdentityValue.<V>value());
    }
    
    @Factory
    public static <V> FluentMatcher<V, V> match(Class<V> c) {
        return new FluentMatcherBuilder<>(IdentityValue.<V>value());
    }
    
    @Factory
    public static <V, M> FluentMatcher<V, M> match(MatchValueAdapter<M, V> adapter) {
        return new FluentMatcherBuilder<>(adapter);
    }
    
    private final List<Matcher<? super Value>> matchers = new ArrayList<>();
    private final MatchValueAdapter<Match, Value> matchValueType;
    private ChainFactory chainFactory = null;
    private boolean xorPossible = true;
    
    private QuickDiagnosingMatcher<Match> result = null;

    public FluentMatcherBuilder(MatchValueAdapter<Match, Value> matchValueType) {
        this.matchValueType = matchValueType;
    }
    
    protected void modify() {
        if (result != null) {
            throw new IllegalStateException("Matcher already created");
        }
    }
    
    protected void ensureChain(ChainFactory f) {
        if (chainFactory == null) {
            chainFactory = f;
            xorPossible = false;
        } else if (chainFactory != f) {
            throw new IllegalStateException(
                    "Chain type is " + chainFactory + 
                    ", but required was " + f);
        }
    }

    protected void _and() {
        ensureChain(AndChainMatcher.FACTORY);
    }
    
    protected void _either() {
        ensureChain(OrChainMatcher.FACTORY);
        xorPossible = true;
    }
    
    protected void _or() {
        ensureChain(OrChainMatcher.FACTORY);
    }
    
    protected void _xor() {
        if (xorPossible) {
            chainFactory = null;
        }
        ensureChain(XOrChainMatcher.FACTORY);
    }

    @Override
    protected This _applyMatcher(Matcher<? super Value> matcher, String prefix, boolean not) {
        matchers.add(CIs.wrap(prefix, not, matcher));
        return _this();
    }

    @Override
    public This and() {
        _and();
        return _this();
    }

    @Override
    public This or() {
        _or();
        return _this();
    }

    @Override
    public This xor() {
        _xor();
        return _this();
    }

    @Override
    public This and(Matcher<? super Value> matcher) {
        _and();
        return _match(matcher);
    }

    @Override
    public This andNot(Matcher<? super Value> matcher) {
        _and();
        _not();
        return _match(matcher);
    }

    @Override
    public This or(Matcher<? super Value> matcher) {
        _or();
        return _match(matcher);
    }

    @Override
    public This orNot(Matcher<? super Value> matcher) {
        _or();
        _not();
        return _match(matcher);
    }

    @Override
    public This xor(Matcher<? super Value> matcher) {
        _xor();
        return _match(matcher);
    }

    @Override
    public This xorNot(Matcher<? super Value> matcher) {
        _xor();
        _not();
        return _match(matcher);
    }

    @Override
    public This and(Value value) {
        _and();
        return _match(value);
    }

    @Override
    public This andNot(Value value) {
        _and();
        _not();
        return _match(value);
    }

    @Override
    public This or(Value value) {
        _or();
        return _match(value);
    }

    @Override
    public This orNot(Value value) {
        _or();
        _not();
        return _match(value);
    }

    @Override
    public This xor(Value value) {
        _xor();
        return _match(value);
    }

    @Override
    public This xorNot(Value value) {
        _xor();
        _not();
        return _match(value);
    }

    @Override
    public <NextProperty> FluentProperty<NextProperty, This> and(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _and();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> FluentProperty<NextProperty, This> andNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _and();
        _not();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> FluentProperty<NextProperty, This> or(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _or();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> FluentProperty<NextProperty, This> orNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _or();
        _not();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> FluentProperty<NextProperty, This> xor(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _xor();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> FluentProperty<NextProperty, This> xorNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter) {
        _xor();
        _not();
        return _adapt(adapter);
    }

    @Override
    public <NextProperty> This and(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _and();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> This andNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _and();
        _not();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> This or(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _or();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> This orNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _or();
        _not();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> This xor(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _xor();
        return _match(adapter, matcher);
    }

    @Override
    public <NextProperty> This xorNot(MatchValueAdapter<? super Value, ? extends NextProperty> adapter, Matcher<? super NextProperty> matcher) {
        _xor();
        _not();
        return _match(adapter, matcher);
    }

    @Override
    public <Value2 extends Value> FluentProperty<Value2, This> as(Class<Value2> clazz) {
        return _as(clazz);
    }

    @Override
    public QuickDiagnosingMatcher<Match> getMatcher() {
        if (result == null) {
            if (chainFactory == null){
                _and();
            }
            result = buildMatcher();
        }
        return result;
    }
    
    protected QuickDiagnosingMatcher<Match> buildMatcher() {
        ChainFactory cf = chainFactory;
        if (cf == null) {
            cf = AndChainMatcher.FACTORY;
        }
        Matcher<Value> m = cf.create(matchers);
        String description = getReason();
        if (description != null) {
            m = new MatcherDescription<>(m, description);
        }
        Matcher<Match> m2 = matchValueType.adapt(m);
        return QuickDiagnose.matcher(m2);
    }

    @Override
    public boolean matches(Object item, Description mismatch) {
        return getMatcher().matches(item, mismatch);
    }

    @Override
    public boolean matches(Object item) {
        return getMatcher().matches(item);
    }

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        getMatcher().describeMismatch(item, mismatchDescription);
    }

    @Override
    public void describeTo(Description description) {
        getMatcher().describeTo(description);
    }

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        return getMatcher().matchResult(item);
    }
    
    @Deprecated
    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
        // Java needs traits
    }

    @Override
    public String toString() {
        if (result == null) {
            return buildMatcher().toString();
        }
        return getMatcher().toString();
    }
}
