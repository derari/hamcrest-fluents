package org.cthul.matchers.fluent.lib;

import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.hamcrest.Description;

/**
 *
 */
public class ProcResultAdapter<T> extends SimpleAdapter<Proc, T> {
    
    private static final ProcResultAdapter INSTANCE = new ProcResultAdapter();
    
    public static <T> ProcResultAdapter<T> result() {
        return INSTANCE;
    }
    
    public static <T> MatchValue<T> resultOf(Proc proc) {
        return INSTANCE.adapt(proc);
    }
    
    public static <T> MatchValue<T> resultOf(Proc proc, Object... args) {
        return resultOf(proc.call(args));
    }
    
    public static <T> MatchValue<T> resultOf(Object instance, String method, Object... args) {
        return resultOf(Procs.invoke(instance, method, args));
    }
    
    public static <T> MatchValue<T> resultOf(Class<?> clazz, String method, Object... args) {
        return resultOf(Procs.invoke(clazz, method, args));
    }

    public static <T> MatchValue<T> resultOf(String method, Object... args) {
        return resultOf(detectClass(1), method, args);
    }
    
    private static Class detectClass(int i) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // 0: getStackTrace
        // 1: detectClass
        // 2: caller
        StackTraceElement caller = stack[i+2];
        try {
            return Class.forName(caller.getClassName());
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(
                    "Could not detect class of " + caller, ex);
        }
    }


    public ProcResultAdapter() {
        super("result", Proc.class);
    }

    @Override
    protected boolean acceptValue(Proc value) {
        return value.hasResult();
    }

    @Override
    protected void describeExpectedToAccept(Proc value, Description description) {
        description.appendText("a proc with result");
    }

    @Override
    protected void describeMismatchOfUnaccapted(Proc value, Description description) {
        description.appendText("threw ")
                .appendValue(value.getException());
    }

    @Override
    protected T adaptValue(Proc v) {
        return (T) v.getResult();
    }
}
