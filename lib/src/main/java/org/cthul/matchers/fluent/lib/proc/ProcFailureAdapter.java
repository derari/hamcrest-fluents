package org.cthul.matchers.fluent.lib.proc;

import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import static org.cthul.matchers.fluent.lib.proc.ProcResultAdapter.*;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.hamcrest.Description;
import org.hamcrest.Factory;

/**
 *
 */
public class ProcFailureAdapter extends SimpleAdapter<Proc, Throwable> {

    private static final ProcFailureAdapter INSTANCE = new ProcFailureAdapter();
    
    @Factory
    public static ProcFailureAdapter thrownException() {
        return INSTANCE;
    }
    
    @Factory
    public static MatchValue<Throwable> thrownBy(Proc proc) {
        return INSTANCE.adapt(proc);
    }
    
    @Factory
    public static MatchValue<Throwable> thrownBy(MatchValue<? extends Proc> proc) {
        return INSTANCE.adapt(proc);
    }
    
    @Factory
    public static <V> MatchValueAdapter<V, Throwable> thrownBy(MatchValueAdapter<V, ? extends Proc> proc) {
        return INSTANCE.adapt(proc);
    }
    
    @Factory
    public static MatchValue<Throwable> thrownBy(Proc proc, Object... args) {
        return thrownBy(proc.call(args));
    }
    
    @Factory
    public static MatchValue<Throwable> thrownBy(Object instance, String method, Object... args) {
        return thrownBy(Procs.invokeWith(instance, method, args));
    }
    
    @Factory
    public static MatchValue<Throwable> thrownBy(Class<?> clazz, String method, Object... args) {
        return thrownBy(Procs.invokeWith(clazz, method, args));
    }

    @Factory
    public static MatchValue<Throwable> thrownBy(String method, Object... args) {
        return thrownBy(detectClass(1), method, args);
    }
    
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
    protected Throwable adaptValue(Proc v) {
        return v.getException();
    }
}
