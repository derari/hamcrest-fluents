package org.cthul.matchers;

import org.cthul.matchers.fluent.FluentMatcher;
import org.cthul.matchers.fluent.builder.FluentMatcherBuilder;
import org.cthul.matchers.fluent.intern.FluentsBase;
import org.cthul.matchers.fluent.value.MatchValueAdapter;

/**
 *
 */
public class Fluents extends FluentsBase {
    
    public static <V> FluentMatcher<V, V> match() {
        return FluentMatcherBuilder.match();
    }
    
    public static <V> FluentMatcher<V, V> match(Class<V> c) {
        return FluentMatcherBuilder.match(c);
    }
    
    public static <V, M> FluentMatcher<V, M> match(MatchValueAdapter<M, V> adapter) {
        return FluentMatcherBuilder.match(adapter);
    }
 
    public static <V> org.cthul.matchers.fluent.value.MatchValueAdapter<V, Integer> arraySizeOf(org.cthul.matchers.fluent.value.MatchValueAdapter<V, ?> adapter) {
      return org.cthul.matchers.fluent.lib.collection.ArraySizeAdapter.<V>arraySizeOf(adapter);
    }

    public static <V> org.cthul.matchers.fluent.value.MatchValueAdapter<V, Integer> sizeOf(org.cthul.matchers.fluent.value.MatchValueAdapter<V, ? extends java.lang.Iterable<?>> adapter) {
      return org.cthul.matchers.fluent.lib.collection.IterableSizeAdapter.<V>sizeOf(adapter);
    }

    public static <V> org.cthul.matchers.fluent.value.MatchValueAdapter<V, Integer> mapSizeOf(org.cthul.matchers.fluent.value.MatchValueAdapter<V, ? extends java.util.Map<?,?>> adapter) {
      return org.cthul.matchers.fluent.lib.collection.MapSizeAdapter.<V>mapSizeOf(adapter);
    }

    public static <V> org.cthul.matchers.fluent.value.MatchValueAdapter<V, Throwable> anyCauseOf(org.cthul.matchers.fluent.value.MatchValueAdapter<V, ? extends java.lang.Throwable> adapter) {
      return org.cthul.matchers.fluent.lib.object.ThrowableAnyCauseAdapter.<V>anyCauseOf(adapter);
    }

    public static <V> org.cthul.matchers.fluent.value.MatchValueAdapter<V, Throwable> causeOf(org.cthul.matchers.fluent.value.MatchValueAdapter<V, ? extends java.lang.Throwable> adapter) {
      return org.cthul.matchers.fluent.lib.object.ThrowableCauseAdapter.<V>causeOf(adapter);
    }

    public static <T> org.cthul.matchers.fluent.value.MatchValueAdapter<org.cthul.proc.Proc, T> resultOfCall(java.lang.Object... args) {
      return org.cthul.matchers.fluent.lib.proc.ProcCallAdapter.<T>resultOfCall(args);
    }

    public static org.cthul.matchers.fluent.value.MatchValueAdapter<org.cthul.proc.Proc, Throwable> thrownByCall(java.lang.Object... args) {
      return org.cthul.matchers.fluent.lib.proc.ProcCallAdapter.thrownByCall(args);
    }

    public static <V, T> org.cthul.matchers.fluent.value.MatchValueAdapter<V, T> resultOf(org.cthul.matchers.fluent.value.MatchValueAdapter<V, ? extends org.cthul.proc.Proc> proc) {
      return org.cthul.matchers.fluent.lib.proc.ProcResultAdapter.<V,T>resultOf(proc);
    }

    public static <V> org.cthul.matchers.fluent.value.MatchValueAdapter<V, Throwable> thrownBy(org.cthul.matchers.fluent.value.MatchValueAdapter<V, ? extends org.cthul.proc.Proc> proc) {
      return org.cthul.matchers.fluent.lib.proc.ProcFailureAdapter.<V>thrownBy(proc);
    }
}
