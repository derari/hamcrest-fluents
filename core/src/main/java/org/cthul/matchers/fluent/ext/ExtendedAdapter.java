package org.cthul.matchers.fluent.ext;

import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

/**
 *
 */
public interface ExtendedAdapter<Value, Property, Fluent, FluentProperty> {
    
    MatchValueAdapter<? super Value, Property> getAdapter();
    
    FluentPropertyFactory<Property, Fluent, FluentProperty> getFactory();
    
    public static interface FluentPropertyFactory<Property, Fluent, FluentProperty> {
        
        FluentProperty create(Matchable<Property, Fluent> matchable);
    }
    
    public static interface Matchable<Property, Fluent> {
        
        Fluent apply(Matcher<? super Property> matcher);
    }
}
