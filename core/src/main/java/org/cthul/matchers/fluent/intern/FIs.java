package org.cthul.matchers.fluent.intern;

import org.cthul.matchers.diagnose.nested.NestedResultMatcher;
import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class FIs<T> extends NestedResultMatcher<T> {

    private final Matcher<? super T> nested;
    private final String prefix;
    private final boolean not;

    public FIs(Matcher<? super T> nested, String prefix, boolean not) {
        this.nested = nested;
        this.prefix = prefix;
        this.not = not;
    }

    public FIs(Matcher<? super T> nested) {
        this(nested, "is", false);
    }

    @Override
    public boolean matches(Object o) {
        return not ^ nested.matches(o);
    }
    
    @Override
    public boolean matches(Object o, Description d) {
        if (matches(o)) return true;
        describeMismatch(o, d);
        return false;
    }

    @Override
    public void describeTo(Description description) {
        if (prefix != null) {
            description.appendText(prefix).appendText(" ");
        }
        if (not) {
            description.appendText("not ");
        }
        nested.describeTo(description);
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_UNARY;
    }

    @Override
    public <I> MatchResult<I> matchResult(I item) {
        final MatchResult<I> result = quickMatchResult(nested, item);
        return new NestedResult<I, FIs<T>>(item, this, not ^ result.matched()) {
            @Override
            public void describeMatch(Description d) {
//                if (prefix != null) {
//                    d.appendText(prefix).appendText(" ");
//                }
//                if (not) {
//                    d.appendText("not ");
//                }
                pastPrefix(prefix, d);
                d.appendText(" ");
                nestedDescribeTo(getMatchPrecedence(), result, d);
            }
            @Override
            public void describeExpected(Description d) {
                if (prefix != null) {
                    d.appendText(prefix).appendText(" ");
                }
                if (not) {
                    d.appendText("not ");
                    nestedDescribeTo(getExpectedPrecedence(), result, d);
                } else {
                    result.getMismatch().describeExpected(d);
                    //nestedDescribeTo(getExpectedPrecedence(), result.getMismatch().getExpectedDescription(), d);
                }
            }
            @Override
            public void describeMismatch(Description d) {
                if (not) {
                    d.appendValue(getValue()).appendText(" ");
                    pastPrefix(prefix, d);
                } 
                nestedDescribeTo(getMismatchPrecedence(), result, d);
            }
        };
    }

    public static void pastPrefix(String prefix, Description description) {
        if (prefix != null) {
            switch (prefix) {
                case "is":
                    description.appendText("was ");
                    break;
                case "has":
                    description.appendText("had ");
                    break;
                default:
                    description.appendText(prefix).appendText(" ");
            }
        }
    }
    
    public static <T> Matcher<T> wrap(String prefix, boolean not, Matcher<T> matcher) {
        if (prefix == null && !not) {
            return matcher;
        } else {
            return new FIs<>(matcher, prefix, not);
        }
    }
    
}
