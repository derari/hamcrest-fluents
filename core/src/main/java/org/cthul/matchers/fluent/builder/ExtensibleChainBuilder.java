package org.cthul.matchers.fluent.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.cthul.matchers.chain.ChainFactory;
import org.cthul.matchers.fluent.FluentStep;
import org.hamcrest.Matcher;

/**
 * Implements a sub-fluent, or chain.
 * <p>
 * A chain is a fluent that is eventually terminated and then returns to
 * the source fluent.
 * <p>
 * The implementation can be used as an invocation handler to implement arbitrary chain interfaces.
 * 
 * @param <Value> value type
 * @param <TheFluent> original fluent type
 */
public class ExtensibleChainBuilder<Value, TheFluent> extends FluentChainBuilder<Value, TheFluent, ExtensibleChainBuilder<Value, TheFluent>> implements InvocationHandler {
    
    public static <Value, TheFluent, Chain> Chain create(Class<Chain> chainClass, Matchable<Value, TheFluent> matchable, ChainFactory chainFactory, String terminalToken) {
        Class<?>[] interfaces = {chainClass};
        ExtensibleChainBuilder builder = new ExtensibleChainBuilder(chainClass, matchable, chainFactory, terminalToken);
        Object proxy = Proxy.newProxyInstance(chainClass.getClassLoader(), interfaces, builder);
        builder.setProxy(proxy);
        return (Chain) proxy;
    }
    
    private final String terminalToken;
    private final Object stepHandler;
    private Object proxy = null;

    protected ExtensibleChainBuilder(Class<?> chainClass, Matchable<Value, TheFluent> matchable, ChainFactory chainFactory, String completionToken) {
        super(matchable, chainFactory);
        this.terminalToken = completionToken;
        this.stepHandler = buildStepHandler(chainClass);
    }

    protected void setProxy(Object proxy) {
        this.proxy = proxy;
    }
    
    private Object buildStepHandler(Class<?> chainClass) {
        Class<?> stepClass = null;
        while (stepClass == null && chainClass != null) {
            for (Class<?> dc: chainClass.getDeclaredClasses()) {
                String name = dc.getSimpleName();
                if (!dc.isInterface() &&
                        (name.equals("Step") || name.equals("Impl"))) {
                    stepClass = dc;
                    break;
                }
            }
            chainClass = chainClass.getDeclaringClass();
        }
        if (stepClass == null) {
            return new FluentStepBuilder<>(matchable());
        }
        try {
            Constructor<?> c = stepClass.getConstructor(Matchable.class);
            return c.newInstance(matchable());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Matchable<Value, Object> matchable() {
        return new Matchable<Value, Object>() {
            @Override
            public Object apply(Matcher<? super Value> matcher) {
                __(matcher);
                if (_isTerminating()) {
                    return _submit();
                } else {
                    return proxy;
                }
            }
        };
    }

    @Override
    protected FluentStep<Value, TheFluent> _terminalStep() {
        return (FluentStep) proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName;
        if (method.getName().equals(terminalToken)) {
            if (args == null || args.length == 0) {
                _terminate();
                return proxy;
            } else {
                _terminate();
                methodName = "__";
            }            
        } else {
            methodName = method.getName();
        }
        Method stepMethod = stepHandler.getClass().getMethod(methodName, method.getParameterTypes());
        return stepMethod.invoke(stepHandler, args);
    }
}
