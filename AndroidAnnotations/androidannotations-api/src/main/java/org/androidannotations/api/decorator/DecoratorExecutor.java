package org.androidannotations.api.decorator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DecoratorExecutor {

    private static ConcurrentMap<Class<? extends DecoratorHandler>, DecoratorHandler> cache =
            new ConcurrentHashMap<Class<? extends DecoratorHandler>, DecoratorHandler>();

    public static <T> T call(Class<? extends DecoratorHandler> clazz, MethodCallable<T> callable) {
        DecoratorHandler handler = cache.get(clazz);
        if (handler == null) {
            try {
                handler = clazz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            DecoratorHandler anotherInstance = cache.putIfAbsent(clazz, handler);
            if (anotherInstance != null) {
                handler = anotherInstance;
            }
        }

        try {
            return handler.call(callable);
        } catch (Exception e) {
            return DecoratorExecutor.<RuntimeException, T>throwException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable, R> R throwException(Throwable exception) throws T
    {
        throw (T) exception;
    }

}
