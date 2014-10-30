package org.cthul.matchers.fluent8;

import org.cthul.matchers.fluent.builder.FailureHandler;
import org.cthul.matchers.fluent.builder.FluentAssertBuilder;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;

/**
 *
 */
public class ActualObjectFluent extends FluentAssertBuilder<Object, ActualObjectFluent>
                implements ObjectFluent<Object, Object, ActualObjectFluent, ActualObjectFluent> {

    public ActualObjectFluent(FailureHandler failureHandler, MatchValue<Object> matchValues) {
        super(failureHandler, matchValues);
    }
    
    public StringFluent<Object, String, ActualObjectFluent, ?> asString() {
        return new StringProperty(null, null, true);
    }
    
    protected class StringProperty extends AssertPropertyBuilder<String, StringProperty>
            implements StringFluent<Object, String, ActualObjectFluent, StringProperty> {

        public StringProperty(MatchValueAdapter<? super Object, String> adapter, String prefix, boolean not) {
            super(adapter, prefix, not);
        }
    }
}
