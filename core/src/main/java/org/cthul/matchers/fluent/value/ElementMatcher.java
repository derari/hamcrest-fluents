package org.cthul.matchers.fluent.value;

import org.cthul.matchers.fluent.intern.FIs;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ExpectationDescription;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 *
 */
public class ElementMatcher<Item> extends TypeSafeMatcher<Element<?>> 
                implements MatchValue.ElementMatcher<Item> {
    
    private final Matcher<? super Item> matcher;

    public ElementMatcher(Matcher<? super Item> matcher, String prefix, boolean not) {
        this(FIs.wrap(prefix, not, matcher));
    }
    
    public ElementMatcher(Matcher<? super Item> matcher) {
        super(Element.class);
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(Element<?> item) {
        return matcher.matches(item.value());
    }

    @Override
    public void describeTo(Description description) {
        matcher.describeTo(description);
    }

    @Override
    public void describeExpected(Element<?> e, ExpectationDescription description) {
        matcher.describeTo(description);
        description.addedExpectation();
    }

    @Override
    protected void describeMismatchSafely(Element<?> item, Description mismatchDescription) {
        matcher.describeMismatch(item.value(), mismatchDescription);
    }

    public Matcher<? super Item> getActualMatcher() {
        return matcher;
    }
    
}
