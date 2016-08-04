package org.androidannotations.api.decorator;

public interface DecoratorHandler {
    <T> T call(MethodCallable<T> callable) throws Exception;
}
