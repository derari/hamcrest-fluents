package org.cthul.matchers.fluent.ext;

import org.cthul.matchers.fluent.adapters.AsTypeAdapter;
import org.cthul.matchers.fluent.builder.Matchable;
import org.cthul.matchers.fluent.value.MatchValueAdapter;
import org.hamcrest.Matcher;

public class Extensions {
    
    public static <V, BF, S> ExtensionFactory<V, S, BF, S> factory(StepFactory<V, BF, S> newStep) {
        return new BasicFactory<>(newStep);
    }
    
    public static <V, F, BF, S> ExtensionFactory<V, F, BF, S> factory(StepFactory<V, BF, S> newStep, FluentFactory<V, F> newFluent) {
        return new BasicFactory<>(newStep, newFluent);
    }
    
    public static <V, NV, F, BF, S> ExtensionAdapter<V, NV, F, BF, S> adapter(MatchValueAdapter<? super V, ? extends NV> adapter, ExtensionFactory<NV, F, BF, S> factory) {
        return new BasicAdapter<>(adapter, factory);
    }
    
    public static <V, NV, F, BF, S> ExtensionFactory<V, F, BF, S> factory(MatchValueAdapter<? super V, ? extends NV> adapter, ExtensionFactory<NV, F, BF, S> factory) {
        return adapter(adapter, factory).asFactory();
    }
    
    public static <V, F, BF, S> ExtensionAdapter<Object, V, F, BF, S> typecastAdapter(Class<V> clazz, ExtensionFactory<V, F, BF, S> factory) {
        return adapter(AsTypeAdapter.as(clazz), factory);
    }
    
    public static <V, BF, S> ExtensionAdapter<Object, V, S, BF, S> typecastAdapter(Class<V> clazz, StepFactory<V, BF, S> newStep) {
        return typecastAdapter(clazz, factory(newStep));
    }
    
    public static <V, F, BF, S> ExtensionAdapter<Object, V, F, BF, S> typecastAdapter(Class<V> clazz, StepFactory<V, BF, S> newStep, FluentFactory<V, F> newFluent) {
        return typecastAdapter(clazz, factory(newStep, newFluent));
    }
    
    public static <V, BF, S> ExtensionFactory<Object, S, BF, S> typecastFactory(Class<V> clazz, StepFactory<V, BF, S> newStep) {
        return typecastAdapter(clazz, newStep).asFactory();
    }
    
    public static <V, F, BF, S> ExtensionFactory<Object, F, BF, S> typecastFactory(Class<V> clazz, StepFactory<V, BF, S> newStep, FluentFactory<V, F> newFluent) {
        return typecastAdapter(clazz, newStep, newFluent).asFactory();
    }
    
    public static <F, BF, S> ExtensionFactory<Object, F, BF, S> uncheckedFactory(MatchValueAdapter adapter, ExtensionFactory<?, F, BF, S> factory) {
        return factory(adapter, factory);
    }
    
    public static <V, NV, F> Matchable<NV, F> adapt(final MatchValueAdapter<? super V, ? extends NV> adapter, final Matchable<? extends V, F> value) {
        return new Matchable<NV, F>() {
            @Override
            public F apply(Matcher<? super NV> matcher) {
                Matcher<? super V> m = adapter.adapt(matcher);
                return value.apply(m);
            }
            @Override
            public String toString() {
                String s = adapter.toString();
                return value.toString() + (s.isEmpty() ? "" : " " + adapter.toString());
            }
        };
    }
    
    public static class BasicAdapter<V, NV, F, BF, S> implements ExtensionAdapter<V, NV, F, BF, S> {
        private final MatchValueAdapter<? super V, ? extends NV> adapter;
        private final ExtensionFactory<NV, F, BF, S> factory;
        private ExtensionFactory<V, F, BF, S> adaptedFactory = null;

        protected BasicAdapter(MatchValueAdapter<? super V, ? extends NV> adapter, ExtensionFactory<NV, F, BF, S> factory) {
            this.adapter = adapter;
            this.factory = factory;
        }

        @Override
        public MatchValueAdapter<? super V, ? extends NV> getAdapter() {
            return adapter;
        }

        @Override
        public ExtensionFactory<NV, F, BF, S> getFactory() {
            return factory;
        }

        @Override
        public ExtensionFactory<V, F, BF, S> asFactory() {
            if (adaptedFactory != null) return adaptedFactory;
            StepFactory<V, BF, S> newStep = new StepFactory<V, BF, S>() {
                @Override
                public S newStep(final Matchable<? extends V, BF> matchable) {
                    Matchable<NV, BF> adaptingMatchable = new Matchable<NV, BF>() {
                        @Override
                        public BF apply(Matcher<? super NV> matcher) {
                            Matcher<? super V> adaptingMatcher = getAdapter().adapt(matcher);
                            return matchable.apply(adaptingMatcher);
                        }
                    };
                    return getFactory().getStepFactory().newStep(adaptingMatchable);
                }
            };
            FluentFactory<V, F> newFluent = new FluentFactory<V, F>() {
                @Override
                public F newFluent(Matchable<? extends V, ?> value) {
                    return getFactory().getFluentFactory().newFluent(adapt(getAdapter(), value));
                }
            };
            return adaptedFactory = new BasicFactory<>(newStep, newFluent);
        }

        @Override
        public <BaseValue> ExtensionAdapter<BaseValue, NV, F, BF, S> adaptTo(MatchValueAdapter<? super BaseValue, ? extends V> adapter) {
            MatchValueAdapter<? super BaseValue, ? extends NV> adapter2 = this.adapter.adapt(adapter);
            return new BasicAdapter<>(adapter2, factory);
        }
    }
    
    public static class BasicFactory<V, F, BF, S> implements ExtensionFactory<V, F, BF, S> {
        private final StepFactory<V, BF, S> newStep;
        private FluentFactory<V, F> newFluent;

        protected BasicFactory(StepFactory<V, BF, S> newStep) {
            this.newStep = newStep;
        }

        protected BasicFactory(StepFactory<V, BF, S> newStep, FluentFactory<V, F> newFluent) {
            this.newStep = newStep;
            this.newFluent = newFluent;
        }

        @Override
        public StepFactory<V, BF, S> getStepFactory() {
            return newStep;
        }

        @Override
        public FluentFactory<V, F> getFluentFactory() {
            if (newFluent != null) return newFluent;
            return newFluent = new FluentFactory<V, F>() {
                @Override
                public F newFluent(Matchable<? extends V, ?> value) {
                    ExtensionFactory<V, F, F, F> self = (ExtensionFactory) BasicFactory.this;
                    final LoopingMatchable<V, F> matchable = new LoopingMatchable<>(value);
                    F fluent = self.getStepFactory().newStep(matchable);
                    matchable.fluent = fluent;
                    return fluent;
                }
            };
        }

        @Override
        public F newFluent(Matchable<? extends V, ?> value) {
            return getFluentFactory().newFluent(value);
        }

        @Override
        public S newStep(Matchable<? extends V, BF> matchable) {
            return getStepFactory().newStep(matchable);
        }

        @Override
        public <BaseValue> ExtensionAdapter<BaseValue, V, F, BF, S> adaptTo(MatchValueAdapter<? super BaseValue, ? extends V> adapter) {
            return adapter(adapter, this);
        }
    }
    
    private static class LoopingMatchable<Value, TheFluent> implements Matchable<Value, TheFluent> {
        private TheFluent fluent = null;
        private final Matchable<? extends Value, ?> value;

        public LoopingMatchable(Matchable<? extends Value, ?> value) {
            this.value = value;
        }

        @Override
        public TheFluent apply(Matcher<? super Value> matcher) {
            value.apply(matcher);
            if (fluent == null) {
                throw new IllegalStateException("Fluent not set");
            }
            return fluent;
        }
    }
}
