package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * A fluent that is not a matcher,
 * but evaluates the conditions as they are added.
 * 
 */
public interface FluentAssert<Value> 
                extends Fluent<Value>, 
                        FluentPropertyAssert<Value, Value> {
    
    @Override
    FluentAssert<Value> as(String reason);

    @Override
    FluentAssert<Value> as(String reason, Object... args);

    @Override
    FluentAssert<Value> is();
    
    @Override
    FluentAssert<Value> has();

    @Override
    FluentAssert<Value> not();
    
    FluentAssert<Value> and();

    FluentAssert<Value> andNot();
    
    @Override
    FluentAssert<Value> _(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> is(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> has(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> not(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> isNot(Matcher<? super Value> matcher);

    @Override
    FluentAssert<Value> hasNot(Matcher<? super Value> matcher);

    FluentAssert<Value> and(Matcher<? super Value> matcher);

    FluentAssert<Value> andNot(Matcher<? super Value> matcher);
    
    @Override
    FluentAssert<Value> all(Matcher<? super Value>... matcher);
    
    @Override
    FluentAssert<Value> any(Matcher<? super Value>... matcher);
    
    @Override
    FluentAssert<Value> none(Matcher<? super Value>... matcher);
    
    @Override
    <P> FluentPropertyAssert<Value, P> _(MatchValueAdapter<? super Value, P> matcher);
    
    @Override
    <P> FluentPropertyAssert<Value, P> has(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <P> FluentPropertyAssert<Value, P> not(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <P> FluentPropertyAssert<Value, P> hasNot(MatchValueAdapter<? super Value, P> adapter);
    
    <P> FluentPropertyAssert<Value, P> and(MatchValueAdapter<? super Value, P> adapter);
    
    <P> FluentPropertyAssert<Value, P> andNot(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <Value2 extends Value> FluentAssert<Value2> isA(Class<Value2> clazz, Matcher<? super Value2> matcher);
    
    @Override
    <Value2 extends Value> IsA<Value2> isA(Class<Value2> clazz);
    
    interface IsA<Value> 
                    extends FluentPropertyAssert.IsA<Value, Value>,
                            Fluent.IsA<Value> {

        @Override
        FluentAssert<Value> that();
        
        @Override
        FluentAssert<Value> thatIs();

        @Override
        FluentAssert<Value> that(Matcher<? super Value> matcher);

        @Override
        FluentAssert<Value> thatIs(Matcher<? super Value> matcher);
    }
}
