package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.nested.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * A hamcrest matcher that uses {@link MatchValueAdapter} to match the value
 * against another matcher.
 * <p>
 * Example: ´length-of-string is less than 3´.matches("xa")
 */
@Deprecated
public class AdaptingMatcher<Value, Property> extends NestedMatcher<Value> {
    
    private final ElementMatcher<Property> matcher;
    private final MatchValueAdapter<Value, Property> adapter;

    private AdaptingMatcher(MatchValueAdapter<Value, Property> adapter, Matcher<? super Property> matcher, String prefix, boolean not) {
        this.adapter = adapter;
        this.matcher = new ElementMatcher<>(-1, matcher, prefix, not);
    }

    private AdaptingMatcher(MatchValueAdapter<Value, Property> adapter, Matcher<? super Property> matcher) {
        this.adapter = adapter;
        this.matcher = new ElementMatcher<>(-1, matcher);
    }

    @Override
    public boolean matches(Object item) {
        return adapter.adapt(item).matches(matcher);
    }

    @Override
    public boolean matches(Object item, Description mismatch) {
        MatchValue<Property> mv = adapter.adapt(item);
        if (mv.matches(matcher)) {
            return true;
        }
//        mv.getMismatch().describeMismatch(mismatch);
        return false;
    }

    @Override
    public void describeTo(Description description) {
//        adapter.describeConsumer(matcher, description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        matches(item, description);
    }

    @Override
    public int getDescriptionPrecedence() {
        return Nested.precedenceOf(matcher);
    }
}
