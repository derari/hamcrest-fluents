package org.cthul.matchers.fluent.lib.proc;

import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.cthul.matchers.proc.Returns;
import org.cthul.proc.Proc;
import org.cthul.proc.Procs;
import org.hamcrest.Factory;

/**
 *
 */
public class ProcResultAdapter<T> extends SimpleAdapter<Proc, T> {
    
    private static final ProcResultAdapter INSTANCE = new ProcResultAdapter();
    
    @Factory
    public static <T> ProcResultAdapter<T> result() {
        return INSTANCE;
    }
    
    @Factory
    public static <T> MatchValue<T> resultOf(Proc proc) {
        return INSTANCE.adapt(proc);
    }
    
    @Factory
    public static <T> MatchValue<T> resultOf(MatchValue<? extends Proc> proc) {
        return INSTANCE.adapt(proc);
    }
    
    @Factory
    public static <V, T> MatchValueAdapter<V, T> resultOf(MatchValueAdapter<V, ? extends Proc> proc) {
        return INSTANCE.adapt(proc);
    }
    
    @Factory
    public static <T> MatchValue<T> resultOf(Proc proc, Object... args) {
        return resultOf(proc.call(args));
    }
    
    @Factory
    public static <T> MatchValue<T> resultOf(Object instance, String method, Object... args) {
        return resultOf(Procs.invokeWith(instance, method, args));
    }
    
    @Factory
    public static <T> MatchValue<T> resultOf(Class<?> clazz, String method, Object... args) {
        return resultOf(Procs.invokeWith(clazz, method, args));
    }

    @Factory
    public static <T> MatchValue<T> resultOf(String method, Object... args) {
        return resultOf(detectClass(1), method, args);
    }
    
    static Class detectClass(int i) {
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
        super("result", Returns.hasResult());
    }

    @Override
    protected T adaptValue(Proc v) {
        return (T) v.getResult();
    }
}
