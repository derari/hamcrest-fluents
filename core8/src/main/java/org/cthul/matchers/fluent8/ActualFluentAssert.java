package org.cthul.matchers.fluent8;

import org.cthul.matchers.fluent.builder.FailureHandler;
import org.cthul.matchers.fluent.builder.FluentAssertBuilder;
import org.cthul.matchers.fluent.value.MatchValue;

public class ActualFluentAssert 
        extends FluentAssertBuilder<Object, ActualFluentAssert>
        implements ObjectFluent<Object, Object, ActualFluentAssert, ActualFluentAssert> {

    public ActualFluentAssert(FailureHandler failureHandler, MatchValue<Object> matchValues) {
        super(failureHandler, matchValues);
    }
}
