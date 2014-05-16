package org.cthul.matchers.fluent.value;

import org.cthul.matchers.object.CIs;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultProxy;
import org.cthul.matchers.diagnose.safe.TypesafeNestedResultMatcher;
import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 * @param <Item>
 */
public class ElementMatcherWrapper<Item> 
                extends TypesafeNestedResultMatcher<Element<?>> 
                implements ElementMatcher<Item> {
    
    private final int index;
    private final Matcher<? super Item> matcher;

    public ElementMatcherWrapper(int index, Matcher<? super Item> matcher, String prefix, boolean not) {
        this(index, CIs.wrap(prefix, not, matcher));
    }
    
    public ElementMatcherWrapper(int index, Matcher<? super Item> matcher) {
        super(Element.class);
        this.index = index;
        this.matcher = matcher;
    }

    @Override
    public int getDescriptionPrecedence() {
        return Nested.precedenceOf(matcher);
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
    public <I> Result<I> matchResult(I item) {
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
                    extends MatchResultProxy<I, ElementMatcherWrapper<?>> 
                    implements Result<I>, ElementMatcher.Mismatch<I> {
        
        public EMMatchResult(MatchResult<?> result, I value, ElementMatcherWrapper<?> matcher) {
            super(result, value, matcher);
        }

        @Override
        public ElementMatcher.Mismatch<I> getMismatch() {
            return (ElementMatcher.Mismatch<I>) super.getMismatch();
        }

        @Override
        public void describeExpected(ExpectationDescription description) {
            description.addExpected(index, getExpectedDescription());
        }
    }
}
