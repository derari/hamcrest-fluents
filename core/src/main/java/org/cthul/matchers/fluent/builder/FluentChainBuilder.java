package org.cthul.matchers.fluent.builder;

import java.util.ArrayList;
import java.util.List;
import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.fluent.FluentStep;
import org.cthul.matchers.object.CIs;
import org.hamcrest.Matcher;

/**
 * Implements a sub-fluent, or chain.
 * <p>
 * A chain is a fluent that is eventually terminated and then returns to
 * the source fluent.
 * <p>
 * The implementation can be used as an invocation handler to implement arbitrary chain interfaces.
 * 
 * @param <Value> value type
 * @param <TheFluent> original fluent type
 * @param <This> this type
 */
public class FluentChainBuilder<Value, TheFluent, This extends FluentChainBuilder<Value, TheFluent, This>> extends AbstractFluentBuilder<Value, This> {
    
    private final List<Matcher<? super Value>> list = new ArrayList<>();
    private final Matchable<Value, TheFluent> matchable;
    private final ChainFactory chainFactory;
    private boolean terminate = false;
    private boolean submitted = false;

    public FluentChainBuilder(Matchable<Value, TheFluent> matchable, ChainFactory chainFactory) {
        this.matchable = matchable;
        this.chainFactory = chainFactory;
    }

    @Override
    protected This _apply(Matcher<? super Value> matcher, String prefix, boolean not) {
        if (submitted) throw new IllegalStateException("Chain is already submitted");
        matcher = CIs.wrap(prefix, not, matcher);
        list.add(matcher);
        return _this();
    }

    public boolean _isTerminating() {
        return terminate;
    }

    protected TheFluent _submit() {
        if (submitted) throw new IllegalStateException("Chain is already submitted");
        submitted = true;
        Matcher<? super Value> matcher = chainFactory.create(list);
        return matchable.apply(matcher);
    }
    
    protected FluentStep<Value, TheFluent> _terminate() {
        if (submitted) throw new IllegalStateException("Chain is already submitted");
        terminate = true;
        return _terminalStep();
    }
    
    protected FluentStep<Value, TheFluent> _terminalStep() {
        Matchable<Value, TheFluent> m = new Matchable<Value, TheFluent>() {
            @Override
            public TheFluent apply(Matcher<? super Value> matcher) {
                __(matcher);
                return _submit();
            }
        };
        return new FluentStepBuilder<>(m);
    }
}
