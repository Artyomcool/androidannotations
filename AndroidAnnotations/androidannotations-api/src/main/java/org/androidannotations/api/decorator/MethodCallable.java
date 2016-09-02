package org.androidannotations.api.decorator;

import java.lang.annotation.Annotation;
import java.util.concurrent.Callable;

public abstract class MethodCallable<T, A extends Annotation> implements Callable<T> {

    private final String methodName;
    private final String className;

    public MethodCallable(String methodName, String className) {
        this.methodName = methodName;
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public abstract int getArgumentCount();

    public abstract String getArgumentName(int index);

    public abstract Object getArgumentValue(int index);

    public abstract A getAnnotation();

}
