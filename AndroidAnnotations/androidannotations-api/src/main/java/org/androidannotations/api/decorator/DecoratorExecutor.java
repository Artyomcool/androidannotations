package org.androidannotations.api.decorator;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DecoratorExecutor {

    private static ConcurrentMap<Class<? extends DecoratorHandler>, DecoratorHandler> cache =
            new ConcurrentHashMap<Class<? extends DecoratorHandler>, DecoratorHandler>();

    public static <T, A extends Annotation> T call(Class<? extends DecoratorHandler> clazz,
                                                   MethodCallable<T, A> callable) {
        DecoratorHandler<?> handler = cache.get(clazz);
        if (handler == null) {
            try {
                handler = clazz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            DecoratorHandler<?> anotherInstance = cache.putIfAbsent(clazz, handler);
            if (anotherInstance != null) {
                handler = anotherInstance;
            }
        }

        try {
            @SuppressWarnings({"unchecked", "rawtypes"})
            T result = (T) handler.call((MethodCallable) callable);
            return result;
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
