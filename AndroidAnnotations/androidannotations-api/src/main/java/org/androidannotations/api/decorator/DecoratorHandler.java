package org.androidannotations.api.decorator;

import java.lang.annotation.Annotation;

public interface DecoratorHandler<A extends Annotation> {
    <T> T call(MethodCallable<T, ? extends A> callable) throws Exception;
}
