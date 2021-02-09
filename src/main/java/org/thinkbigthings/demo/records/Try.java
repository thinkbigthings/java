package org.thinkbigthings.demo.records;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.toList;


record Try<R>(Exception exception, R result) {

    public Try {
        if((exception == null && result == null) || (exception != null && result != null)) {
            throw new IllegalArgumentException("Must have exactly one non-null argument");
        }
    }

    public static <T, R> Function<T, Try<R>> tryCatch(CheckedFunction<T, R> function) {
        return t -> {
            try {
                R result = Objects.requireNonNull(function.apply(t));
                return new Try<>(null, result);
            } catch (Exception ex) {
                return new Try<>(ex, null);
            }
        };
    }

    public static <T> Collector<Try<T>, ?, List<Exception>> toExceptions() {
        return flatMapping((Try<T> t) -> Stream.ofNullable(t.exception()), toList());
    }

    public static <T> Collector<Try<T>, ?, List<T>> toResults() {
        return flatMapping((Try<T> t) -> Stream.ofNullable(t.result()), toList());
    }

}
