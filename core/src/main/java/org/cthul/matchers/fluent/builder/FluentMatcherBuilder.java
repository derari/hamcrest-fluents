package org.cthul.matchers.fluent.builder;

import java.util.ArrayList;
import java.util.List;
import org.cthul.matchers.CIs;
import org.cthul.matchers.chain.*;
import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.cthul.matchers.diagnose.nested.MatcherDescription;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.FluentPropertyMatcher;
import org.cthul.matchers.fluent.adapters.IdentityValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.*;

/**
 *
 */
public class FluentMatcherBuilder
                <Value, Match, This extends FluentMatcherBuilder<Value, Match, This>>
                extends AbstractPropertyMatcherBuilder<Value, Value, Match, This, This>
                implements FluentMatcher<Value, Match> {
    
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
    }
    
    protected void _or() {
        ensureChain(OrChainMatcher.FACTORY);
        xorPossible = false;
    }
    
    protected void _xor() {
        if (xorPossible && chainFactory == OrChainMatcher.FACTORY) {
            chainFactory = XOrChainMatcher.FACTORY;
        } else {
            ensureChain(XOrChainMatcher.FACTORY);
        }
    }

    @Override
    protected This _applyMatcher(Matcher<? super Value> matcher, String prefix, boolean not) {
        matchers.add(CIs.wrap(prefix, not, matcher));
        return _this();
    }

    @Override
    protected This _updateMatcher(Matcher<? super Value> matcher, String prefix, boolean not) {
        // ignore, matcher is in stored in list already
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
    public This or(Matcher<? super Value> matcher) {
        _or();
        return _match(matcher);
    }

    @Override
    public This xor(Matcher<? super Value> matcher) {
        _xor();
        return _match(matcher);
    }

    @Override
    public <P> FluentPropertyMatcher<Value, P, Match> and(MatchValueAdapter<? super Value, P> adapter) {
        _and();
        return _adapt(adapter);
    }

    @Override
    public <P> FluentPropertyMatcher<Value, P, Match> or(MatchValueAdapter<? super Value, P> adapter) {
        _or();
        return _adapt(adapter);
    }

    @Override
    public <P> FluentPropertyMatcher<Value, P, Match> xor(MatchValueAdapter<? super Value, P> adapter) {
        _xor();
        return _adapt(adapter);
    }

    @Override
    public <P> FluentMatcher<Value, Match> and(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher) {
        _and();
        return _match(adapter, matcher);
    }

    @Override
    public <P> FluentMatcher<Value, Match> or(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher) {
        _or();
        return _match(adapter, matcher);
    }

    @Override
    public <P> FluentMatcher<Value, Match> xor(MatchValueAdapter<? super Value, P> adapter, Matcher<? super P> matcher) {
        _xor();
        return _match(adapter, matcher);
    }

    @Override
    public <Value2 extends Value> FluentMatcher<Value, Match> isA(Class<Value2> clazz, Matcher<? super Value2> matcher) {
        return (FluentMatcher) super.isA(clazz, matcher);
    }

    @Override
    public <Property extends Value> FluentPropertyMatcher.IsA<Value, Property, Match> isA(Class<Property> clazz) {
        return (FluentPropertyMatcher.IsA) super.isA(clazz);
    }

    @Override
    public QuickDiagnosingMatcher<Match> getMatcher() {
        if (result == null) {
            if (chainFactory == null) {
                and();
            }
            Matcher<Value> m = chainFactory.create(matchers);
            String description = getReason();
            if (description != null) {
                m = new MatcherDescription<>(m, description);
            }
            Matcher<Match> m2 = matchValueType.adapt(m);
            result = QuickDiagnose.matcher(m2);
        }
        return result;
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
            String s = getMatcher().toString();
            result = null;
            return s;
        }
        return getMatcher().toString();
    }
}
