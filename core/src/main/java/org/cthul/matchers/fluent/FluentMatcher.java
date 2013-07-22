package org.cthul.matchers.fluent;

import org.cthul.matchers.diagnose.QuickDiagnosingMatcher;
import org.hamcrest.Matcher;

/**
 * Fluent builder for matchers.
 * @param <Value> 
 */
public interface FluentMatcher<Value, Match> 
                extends Fluent<Value>, 
                        FluentPropertyMatcher<Value, Value, Match>,
                        QuickDiagnosingMatcher<Match> {
    
    QuickDiagnosingMatcher<Match> getMatcher();

//    @Override
//    FluentMatcher<Value, Match> as(String reason);
//
//    @Override
//    FluentMatcher<Value, Match> as(String reason, Object... args);

    @Override
    FluentMatcher<Value, Match> is();
    
    @Override
    FluentMatcher<Value, Match> has();

    @Override
    FluentMatcher<Value, Match> not();
    
    @Override
    FluentMatcher<Value, Match> _(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> is(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> has(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> not(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> isNot(Matcher<? super Value> matcher);

    @Override
    FluentMatcher<Value, Match> hasNot(Matcher<? super Value> matcher);

    FluentMatcher<Value, Match> and();

    FluentMatcher<Value, Match> andNot();
    
    FluentMatcher<Value, Match> or();

    FluentMatcher<Value, Match> orNot();
    
    FluentMatcher<Value, Match> xor();

    FluentMatcher<Value, Match> xorNot();
    
    FluentMatcher<Value, Match> and(Matcher<? super Value> matcher);

    FluentMatcher<Value, Match> andNot(Matcher<? super Value> matcher);
    
    FluentMatcher<Value, Match> or(Matcher<? super Value> matcher);

    FluentMatcher<Value, Match> orNot(Matcher<? super Value> matcher);
    
    FluentMatcher<Value, Match> xor(Matcher<? super Value> matcher);

    FluentMatcher<Value, Match> xorNot(Matcher<? super Value> matcher);
    
    @Override
    FluentMatcher<Value, Match> all(Matcher<? super Value>... matcher);
    
    @Override
    FluentMatcher<Value, Match> any(Matcher<? super Value>... matcher);
    
    @Override
    FluentMatcher<Value, Match> none(Matcher<? super Value>... matcher);
    
    @Override
    FluentPropertyMatcher.Both<Value, Value, Match> both(Matcher<? super Value> matcher);
    
    @Override
    FluentPropertyMatcher.Either<Value, Value, Match> either(Matcher<? super Value>... matchers);
    
    @Override
    <Value2 extends Value> FluentMatcher<Value2, Match> isA(Class<Value2> clazz, Matcher<? super Value2> matcher);
    
    @Override
    <Value2 extends Value> IsA<Value2, Match> isA(Class<Value2> clazz);
    
    interface IsA<Value, Match> 
                    extends FluentPropertyMatcher.IsA<Value, Value, Match>,
                            Fluent.IsA<Value> {

        @Override
        FluentMatcher<Value, Match> that();
        
        @Override
        FluentMatcher<Value, Match> thatIs();

        @Override
        FluentMatcher<Value, Match> that(Matcher<? super Value> matcher);

        @Override
        FluentMatcher<Value, Match> thatIs(Matcher<? super Value> matcher);
    }
    
}
