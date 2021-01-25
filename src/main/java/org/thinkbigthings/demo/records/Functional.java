package org.thinkbigthings.demo.records;

import java.util.function.Function;

public class Functional {

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }

    public static <T, R> Function<T, R> uncheck(CheckedFunction<T, R> checkedFunction) {
        return t -> {
            try {
                return checkedFunction.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
