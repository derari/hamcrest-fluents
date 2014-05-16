package org.cthul.matchers.fluent.intern;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 */
public class SwitchInvocationHandler implements InvocationHandler {

    private final Object[] impls;

    public SwitchInvocationHandler(Object... impls) {
        this.impls = impls;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for (Object o: impls) {
            try {
                Method m = o.getClass().getMethod(method.getName(), method.getParameterTypes());
                return m.invoke(o, args);
            } catch (NoSuchMethodException e) {
                // try next
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }
        throw new NoSuchMethodException(method.getName() + " " + Arrays.toString(args));
    }
}
