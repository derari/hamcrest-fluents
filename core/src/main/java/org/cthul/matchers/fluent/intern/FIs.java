package org.cthul.matchers.fluent.intern;

import org.cthul.matchers.diagnose.NestedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class FIs<T> extends NestedMatcher<T> {

    private final Matcher<? super T> nested;
    private final String prefix;
    private final boolean not;
    private int p = -1;

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
    public void describeMismatch(Object item, Description description) {
//        pastPrefix(prefix, description);
        if (not) {
            description.appendValue(item).appendText(" ");
            pastPrefix(prefix, description);
            nested.describeTo(description);
        } else {
            nested.describeMismatch(item, description);
        }
    }

    @Override
    public int getPrecedence() {
        if (p < 0) p = precedenceOf(nested);
        return p;
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
