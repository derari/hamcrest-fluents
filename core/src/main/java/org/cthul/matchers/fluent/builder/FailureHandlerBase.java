package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.value.Expectation;
import org.cthul.matchers.fluent.value.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

public abstract class FailureHandlerBase implements FailureHandler {

    protected String getMismatchString(String reason, MatchValue<?> actual) {
        Description description = new StringDescription();
        if (reason != null) {
            description.appendText(reason).appendText("\n");
        } else {
//            actual.describeTo(description).appendText("\n");
        }
        description.appendText("Expected that ");
        actual.describeValueType(description);
        description.appendText(" ");
        Expectation e = new Expectation();
        actual.getMismatch().describeExpected(e);
        description.appendDescriptionOf(e);
        
        description.appendText("\n     but ");
        actual.getMismatch().describeMismatch(description);
        
        return description.toString();
    }
    
}
