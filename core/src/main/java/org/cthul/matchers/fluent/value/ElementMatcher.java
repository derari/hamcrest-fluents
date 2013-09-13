package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultProxy;
import org.cthul.matchers.diagnose.safe.TypesafeNestedResultMatcher;
import org.cthul.matchers.fluent.intern.FIs;
import org.cthul.matchers.fluent.value.MatchValue.Element;
import org.cthul.matchers.fluent.value.MatchValue.ExpectationDescription;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

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
    public <I> MatchValue.Result<I> matchResult(I item) {
        return new EMMatchResult<>(super.matchResult(item), item, this);
    }

    @Override
    protected <I extends Element<?>> MatchResult<I> matchResultSafely(I item) {
        return (MatchResult) quickMatchResult(matcher, item.value());
    }
    
    public Matcher<? super Item> getActualMatcher() {
        return matcher;
    }
    
    protected class EMMatchResult<I> 
                    extends MatchResultProxy<I, ElementMatcher<?>> 
                    implements MatchValue.Result<I>, MatchValue.Mismatch<I> {
        
        public EMMatchResult(MatchResult<?> result, I value, ElementMatcher<?> matcher) {
            super(result, value, matcher);
        }

        @Override
        public MatchValue.Mismatch<I> getMismatch() {
            return (MatchValue.Mismatch<I>) super.getMismatch();
        }

        @Override
        public void describeExpected(ExpectationDescription description) {
            description.addExpected(index, getExpectedDescription());
        }
    }
}
