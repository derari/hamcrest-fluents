package org.cthul.matchers.fluent.value;

import org.cthul.matchers.fluent.builder.FIs;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ExpectationDescription;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 *
 */
public class ElementMatcher<Item> extends TypeSafeMatcher<Element<Item>> 
                implements MatchValue.ElementMatcher<Item> {
    
    private final Matcher<? super Item> matcher;

    public ElementMatcher(Matcher<? super Item> matcher, String prefix, boolean not) {
        this.matcher = FIs.wrap(prefix, not, matcher);
    }
    
    public ElementMatcher(Matcher<? super Item> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(Element<Item> item) {
        return matcher.matches(item.value());
    }

    @Override
    public void describeTo(Description description) {
        matcher.describeTo(description);
    }

    @Override
    public void describeExpected(Element<Item> e, ExpectationDescription description) {
        matcher.describeTo(description);
        description.addedExpectation();
    }

    @Override
    protected void describeMismatchSafely(Element<Item> item, Description mismatchDescription) {
        matcher.describeMismatch(item.value(), mismatchDescription);
    }

    public Matcher<? super Item> getActualMatcher() {
        return matcher;
    }
    
}