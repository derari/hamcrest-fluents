package org.cthul.matchers.fluent.gen.test;

import java.util.Collection;
import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class Matcher2<T extends Runnable> implements Matcher<T> {
    
    @Factory
    public static ListMatcher listMatcher2(int i) {
        return null;
    }
    
    /**
     * matches and does stuff.
     * @param <T>
     * @param <X>
     * @param t
     * @param x
     * @return 
     */
    @Factory
    public static <T extends Runnable, X> Matcher2<T> matcher2(T t, List<? super X> x) {
        return null;
    }
    
    @Factory
    public static <T extends Collection<?>, X> Matcher<T> matcherX(T t, X x) {
        return null;
    }

    @Override
    public boolean matches(Object item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void describeMismatch(Object item, Description mismatchDescription) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void describeTo(Description description) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static interface ListMatcher extends Matcher<List<?>> {
        
    }
}