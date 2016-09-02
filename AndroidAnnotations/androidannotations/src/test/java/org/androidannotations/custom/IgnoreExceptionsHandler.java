package org.androidannotations.custom;

import org.androidannotations.api.decorator.DecoratorHandler;
import org.androidannotations.api.decorator.MethodCallable;

public class IgnoreExceptionsHandler implements DecoratorHandler {
    @Override
    public Object call(MethodCallable callable) throws Exception {
        try {
            return callable.call();
        } catch (Exception ignored) {
            return null;
        }
    }
}
