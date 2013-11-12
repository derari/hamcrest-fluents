package org.cthul.matchers.fluent.lib.proc;

import org.cthul.matchers.fluent.adapters.SimpleAdapter;
import org.cthul.matchers.fluent.value.MatchValue;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.cthul.proc.Proc;
import org.hamcrest.Description;
import org.hamcrest.Factory;

/**
 *
 */
public class ProcCallAdapter extends SimpleAdapter<Proc, Proc> {

    @Factory
    public static ProcCallAdapter call(Object... args) {
        return new ProcCallAdapter(args);
    }
    
    @Factory
    public static MatchValue<Proc> call(MatchValue<? extends Proc> proc, Object... args) {
        return call(args).adapt(proc);
    }
    
    //@Factory
    public static <T> MatchValueAdapter<Proc, T> resultOfCall(Object... args) {
        return ProcResultAdapter.resultOf(call(args));
    }
    
    //@Factory
    public static MatchValueAdapter<Proc, Throwable> thrownByCall(Object... args) {
        return ProcFailureAdapter.thrownBy(call(args));
    }

    private final Object[] args;
    
    public ProcCallAdapter(Object... args) {
        super("call", Proc.class);
        this.args = args;
    }

    @Override
    public void describeTo(Description description) {
        super.describeTo(description);
        if (args == null) return;
        description.appendText("(");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                description.appendText(",");
            }
            description.appendText(String.valueOf(args[i]));
        }
        description.appendText(")");
    }

    @Override
    protected Proc adaptValue(Proc v) {
        return v.call(args);
    }
}
