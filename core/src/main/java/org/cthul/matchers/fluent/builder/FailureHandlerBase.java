package org.cthul.matchers.fluent.builder;

import org.cthul.matchers.fluent.value.MatchValue;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

/**
 * Base class for {@link FailureHandler} implementations.
 */
public abstract class FailureHandlerBase implements FailureHandler {

    /**
     * Produces default mismatch string.
     * @param reason
     * @param actual
     * @return mismatch string
     */
    protected String getMismatchString(String reason, MatchValue<?> actual) {
        Description description = new StringDescription();
        if (reason != null) {
            description.appendText(reason).appendText("\n");
        } else {
//            actual.describeTo(description).appendText("\n");
        }
        description.appendText("Expected: ");
        actual.describeValueType(description);
        description.appendText(" ");
        actual.matchResult().getMismatch().describeExpected(description);
        
        description.appendText("\n     but: ");
        actual.matchResult().getMismatch().describeMismatch(description);
        
        return description.toString();
    }
}
