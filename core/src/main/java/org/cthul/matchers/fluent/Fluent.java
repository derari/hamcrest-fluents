package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent matcher chain, is either a {@link FluentAssert} or a {@link FluentMatcher}.
 * <p>
 * As a {@linkplain FluentProperty property}, it matches against the
 * value itself.
 * 
 * @param <Value> 
 */
public interface Fluent<Value> extends FluentProperty<Value, Value> {

    @Override
    Fluent<Value> as(String reason);

    @Override
    Fluent<Value> as(String reason, Object... args);

    @Override
    Fluent<Value> is();
    
    @Override
    Fluent<Value> has();

    @Override
    Fluent<Value> not();
    
    @Override
    Fluent<Value> _(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> is(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> has(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> not(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> isNot(Matcher<? super Value> matcher);

    @Override
    Fluent<Value> hasNot(Matcher<? super Value> matcher);

//    Fluent<Value> and();
//
//    Fluent<Value> andNot();
//    
//    Fluent<Value> and(Matcher<? super Value> matcher);
//
//    Fluent<Value> andNot(Matcher<? super Value> matcher);
    
    @Override
    Fluent<Value> all(Matcher<? super Value>... matcher);
    
    @Override
    Fluent<Value> any(Matcher<? super Value>... matcher);
    
    @Override
    Fluent<Value> none(Matcher<? super Value>... matcher);
    
    @Override
    <P> FluentProperty<Value, P> _(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <P> FluentProperty<Value, P> has(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <P> FluentProperty<Value, P> not(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <P> FluentProperty<Value, P> hasNot(MatchValueAdapter<? super Value, P> adapter);
    
    @Override
    <Value2 extends Value> Fluent<Value2> isA(Class<Value2> clazz, Matcher<? super Value2> matcher);
    
    @Override
    <Value2 extends Value> IsA<Value2> isA(Class<Value2> clazz);

    interface IsA<Value> extends FluentProperty.IsA<Value, Value> {
        
        @Override
        Fluent<Value> that();
        
        @Override
        Fluent<Value> thatIs();

        @Override
        Fluent<Value> that(Matcher<? super Value> matcher);
        
        @Override
        Fluent<Value> thatIs(Matcher<? super Value> matcher);
    }
    
}
