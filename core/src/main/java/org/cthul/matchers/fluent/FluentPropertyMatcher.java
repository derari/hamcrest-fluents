package org.cthul.matchers.fluent;

import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 * Fluent Matcher that matches a property of a value.
 * After the match, the chain goes back to the value itself.
 * 
 * @param <Value, Property> 
 */
public interface FluentPropertyMatcher<Value, Property, Match> 
                extends FluentProperty<Value, Property> {

//    @Override
//    FluentPropertyMatcher<Value, Property, Match> as(String reason);
//
//    @Override
//    FluentPropertyMatcher<Value, Property, Match> as(String reason, Object... args);

    @Override
    FluentPropertyMatcher<Value, Property, Match> is();

    @Override
    FluentPropertyMatcher<Value, Property, Match> has();

    @Override
    FluentPropertyMatcher<Value, Property, Match> not();
    
    @Override
    FluentMatcher<Value, Match> _(Matcher<? super Property> matcher);

    @Override
    FluentMatcher<Value, Match> is(Matcher<? super Property> matcher);
    
    @Override
    FluentMatcher<Value, Match> has(Matcher<? super Property> matcher);

    @Override
    FluentMatcher<Value, Match> not(Matcher<? super Property> matcher);
    
    @Override
    FluentMatcher<Value, Match> isNot(Matcher<? super Property> matcher);
    
    @Override
    FluentMatcher<Value, Match> hasNot(Matcher<? super Property> matcher);

    @Override
    FluentMatcher<Value, Match> all(Matcher<? super Property>... matcher);
    
    @Override
    FluentMatcher<Value, Match> any(Matcher<? super Property>... matcher);
    
    @Override
    FluentMatcher<Value, Match> none(Matcher<? super Property>... matcher);
    
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> _(MatchValueAdapter<? super Property, P> matcher);
    
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> has(MatchValueAdapter<? super Property, P> adapter);
    
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> not(MatchValueAdapter<? super Property, P> adapter);
    
    @Override
    <P> FluentPropertyMatcher<Value, P, Match> hasNot(MatchValueAdapter<? super Property, P> adapter);
    
    @Override
    Both<Value, Property, Match> both(Matcher<? super Property> matcher);
    
    interface Both<Value, Property, Match> extends FluentProperty.Both<Value, Property> {
        
        @Override
        FluentMatcher<Value, Match> and(Matcher<? super Property> matcher);
    }
    
    @Override
    Either<Value, Property, Match> either(Matcher<? super Property>... matchers);
    
    interface Either<Value, Property, Match> extends FluentProperty.Either<Value, Property> {
        
        @Override
        FluentMatcher<Value, Match> or(Matcher<? super Property> matcher);
        
        @Override
        FluentMatcher<Value, Match> xor(Matcher<? super Property> matcher);
    }
    
    @Override
    Neither<Value, Property, Match> neither(Matcher<? super Property>... matchers);
    
    interface Neither<Value, Property, Match> extends FluentProperty.Neither<Value, Property> {
        
        @Override
        FluentMatcher<Value, Match> nor(Matcher<? super Property> matcher);
    }
    
    @Override
    <Property2 extends Property> FluentMatcher<? extends Value, Match> isA(Class<Property2> clazz, Matcher<? super Property2> matcher);
    
    @Override
    <Property2 extends Property> IsA<? extends Value, Property2, Match> isA(Class<Property2> clazz);
    
    interface IsA<Value, Property, Match> 
                    extends FluentProperty.IsA<Value, Property>,
                            FluentMatcher<Value, Match> {

        @Override
        FluentPropertyMatcher<Value, Property, Match> that();
        
        @Override
        FluentPropertyMatcher<Value, Property, Match> thatIs();

        @Override
        FluentMatcher<Value, Match> that(Matcher<? super Property> matcher);

        @Override
        FluentMatcher<Value, Match> thatIs(Matcher<? super Property> matcher);
    }
}