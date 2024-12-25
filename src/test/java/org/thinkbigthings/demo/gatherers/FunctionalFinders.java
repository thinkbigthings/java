package org.thinkbigthings.demo.gatherers;


import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FunctionalFinders {


    /**
     * Use this when having more than one element in the stream would be an error.
     *
     * Optional&lt;User&gt; resultUser = users.stream()
     *      .filter(user -&gt; user.getId() == 100)
     *      .collect(findOne());
     *
     * @param <T> Type
     * @return Collection
     */
    public static <T> Collector<T, ?, Optional<T>> toOne() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() > 1) {
                        String m = "Must have zero or one element, found " + list.size();
                        throw new IllegalArgumentException(m);
                    }
                    return list.size() == 1 ? Optional.of(list.get(0)) : Optional.empty();
                }
        );
    }

    /**
     * Use this when not having exactly one element in the stream would be an error.
     *
     * Usage:
     *
     * User resultUser = users.stream()
     *      .filter(user -&gt; user.getId() == 100)
     *      .collect(findExactlyOne());
     *
     * @param <T> Type
     * @return exactly one element.
     */
    public static <T> Collector<T, ?, T> toExactlyOne() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        String m = "Must have exactly one element, found " + list.size();
                        throw new IllegalArgumentException(m);
                    }
                    return list.get(0);
                }
        );
    }
}
