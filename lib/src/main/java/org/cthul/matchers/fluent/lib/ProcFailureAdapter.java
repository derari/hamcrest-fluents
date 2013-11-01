package org.cthul.matchers.fluent.lib;

import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.proc.Proc;
import org.hamcrest.Description;

/**
 *
 */
public class ProcFailureAdapter<T> extends SimpleAdapter<Proc, T> {

    public ProcFailureAdapter() {
        super("exception", Proc.class);
    }

    @Override
    protected boolean acceptValue(Proc value) {
        return !value.hasResult();
    }

    @Override
    protected void describeExpectedToAccept(Proc value, Description description) {
        description.appendText("a proc that failed");
    }

    @Override
    protected void describeMismatchOfUnaccapted(Proc value, Description description) {
        description.appendText("returned ")
                .appendValue(value.getResult());
    }

    @Override
    protected T adaptValue(Proc v) {
        return (T) v.getException();
    }
}
