package org.androidannotations.api.decorator;

import java.util.concurrent.Callable;

public abstract class MethodCallable<T> implements Callable<T> {

    private final String methodName;

    public MethodCallable(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public abstract int getArgumentCount();

    public abstract String getArgumentName(int index);

    public abstract Object getArgumentValue(int index);

}
