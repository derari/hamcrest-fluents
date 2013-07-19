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

    @Override
    FluentPropertyMatcher<Value, Property, Match> as(String reason);

    @Override
    FluentPropertyMatcher<Value, Property, Match> as(String reason, Object... args);

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