package org.androidannotations.test15.decorator;

import org.androidannotations.api.decorator.DecoratorHandler;
import org.androidannotations.api.decorator.MethodCallable;

import java.util.ArrayList;
import java.util.List;

public class LogDecoratorHandler implements DecoratorHandler {

    public static final List<String> logs = new ArrayList<String>();

    @Override
    public <T> T call(MethodCallable<T> callable) throws Exception {
        T result = callable.call();

        StringBuilder log = new StringBuilder();
        log.append(callable.getMethodName()).append('(');
        for (int i = 0; i < callable.getArgumentCount(); i++) {
            log.append(callable.getArgumentName(i)).append(':').append(callable.getArgumentValue(i));
            if (i != callable.getArgumentCount() - 1) {
                log.append(',');
            }
        }
        log.append("): ").append(result);

        logs.add(log.toString());
        return result;
    }

}
