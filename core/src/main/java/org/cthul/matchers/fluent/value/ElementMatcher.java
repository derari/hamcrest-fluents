package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeNestedResultMatcher;
import org.cthul.matchers.diagnose.safe.TypesafeQuickResultMatcherBase;
import org.cthul.matchers.fluent.intern.FIs;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ExpectationDescription;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 *
 */
public class ElementMatcher<Item> extends TypesafeNestedResultMatcher<Element<?>> 
                implements MatchValue.ElementMatcher<Item> {
    
    private final int index;
    private final Matcher<? super Item> matcher;

    public ElementMatcher(int index, Matcher<? super Item> matcher, String prefix, boolean not) {
        this(index, FIs.wrap(prefix, not, matcher));
    }
    
    public ElementMatcher(int index, Matcher<? super Item> matcher) {
        super(Element.class);
        this.index = index;
        this.matcher = matcher;
    }

    @Override
    public int getDescriptionPrecedence() {
        return Nested.precedenceOf(matcher);
    }

    @Override
    public int getMismatchPrecedence() {
        return Nested.mismatchPrecedenceOf(matcher);
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
        description.addExpected(index, this);
    }

    @Override
    protected void describeMismatchSafely(Element<?> item, Description mismatchDescription) {
        matcher.describeMismatch(item.value(), mismatchDescription);
    }

    @Override
    protected <I extends Element<?>> MatchResult<I> matchResultSafely(I item) {
        final MatchResult<?> result = quickMatchResult(matcher, item.value());
        return new NestedResult<I, ElementMatcher<?>>(item, this, result.matched()) {
            @Override
            public int getMatchPrecedence() {
                return result.getMatch().getMatchPrecedence();
            }
            @Override
            public void describeMatch(Description d) {
                result.getMatch().describeMatch(d);
            }
            @Override
            public int getExpectedPrecedence() {
                return result.getMismatch().getExpectedPrecedence();
            }
            @Override
            public void describeExpected(Description d) {
                if (d instanceof ExpectationDescription) {
                    ExpectationDescription ex = (ExpectationDescription) d;
                    ex.addExpected(index, result.getMismatch().getExpectedDescription());
                } else {
                    throw new IllegalArgumentException("Expected ExpectationDescription, got " + d);
                    //super.describeExpected(d);
                }
            }
            @Override
            public int getMismatchPrecedence() {
                return result.getMismatch().getMismatchPrecedence();
            }
            @Override
            public void describeMismatch(Description d) {
                result.getMismatch().describeMismatch(d);
            }
        };
    }
    
    public Matcher<? super Item> getActualMatcher() {
        return matcher;
    }
    
}
