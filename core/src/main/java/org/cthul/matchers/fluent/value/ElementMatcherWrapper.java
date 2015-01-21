package org.cthul.matchers.fluent.value;

import org.cthul.matchers.diagnose.QuickDiagnose;
import org.cthul.matchers.diagnose.QuickDiagnosingMatcherBase;
import org.cthul.matchers.diagnose.QuickResultMatcherBase;
import org.cthul.matchers.diagnose.SelfDescribingBase;
import org.cthul.matchers.diagnose.nested.Nested;
import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.result.AbstractMatchResult;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.result.MatchResultProxy;
import org.cthul.matchers.fluent.value.ElementMatcher.Element;
import org.cthul.matchers.object.CIs;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

/**
 * Creates an {@link ElementMatcher} from a regular {@link Matcher}.
 * @param <Item>
 */
public class ElementMatcherWrapper<Item> extends SelfDescribingBase implements ElementMatcher<Item> {
    
    private final int index;
    private final Matcher<? super Item> matcher;

    public ElementMatcherWrapper(int index, Matcher<? super Item> matcher, String prefix, boolean not) {
        this(index, CIs.wrap(prefix, not, matcher));
    }
    
    public ElementMatcherWrapper(int index, Matcher<? super Item> matcher) {
        this.index = index;
        this.matcher = matcher;
    }

    @Override
    public void describeTo(Description description) {
        matcher.describeTo(description);
    }

    @Override
    public int getDescriptionPrecedence() {
        return Nested.precedenceOf(matcher);
    }

    @Override
    public boolean matches(Element<?> element) {
        return matcher.matches(element.value());
    }

    @Override
    public Result matchResult(Element<?> element) {
        MatchResult<?> result = QuickDiagnose.matchResult(matcher, element.value());
        return asElementResult(index, result);
    }
    
    public Matcher<? super Item> getActualMatcher() {
        return matcher;
    }
    
    public static Result asElementResult(final MatchResult<?> result) {
        return asElementResult(-1, result);
    }
    
    public static Result asElementResult(final int index, final MatchResult<?> result) {
        return new Result() {
            @Override
            public boolean matched() {
                return result.matched();
            }
            @Override
            public void describeTo(Description description) {
                result.describeTo(description);
            }
            @Override
            public void describeExpected(ExpectationDescription description) {
                if (matched()) {
                    description.addExpected(index, result.getMatcher());
                } else {
                    description.addExpected(index, result.getMismatch().getExpectedDescription());
                }
            }
            @Override
            public String toString() {
                return new StringDescription().appendDescriptionOf(this).toString();
            }
        };
    }
    
    public static <T> Matcher<T> asMatcher(final ElementMatcher<T> em) {
        if (em instanceof ElementMatcherWrapper) {
            return ((ElementMatcherWrapper) em).getActualMatcher();
        }
        return new NestedResultMatcher<T>() {
            @Override
            public <I> MatchResult<I> matchResult(final I item) {
                Element<I> element = new Element<I>() {
                    @Override
                    public I value() {
                        return item;
                    }
                };
                final Result r = em.matchResult(element);
                return new AbstractMatchResult<I, Matcher<T>>(item, this, r.matched(), r) {
                    @Override
                    public void describeExpected(Description d) {
                        new ExpectationStringDescription().addExpected(r).describeTo(d);
                    }
                };
            }

            @Override
            public void describeTo(Description description) {
                em.describeTo(description);
            }

            @Override
            public int getDescriptionPrecedence() {
                return em.getDescriptionPrecedence();
            }
        };
    }
}
