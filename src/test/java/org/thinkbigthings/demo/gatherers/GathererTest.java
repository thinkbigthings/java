package org.thinkbigthings.demo.gatherers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;

import static java.util.stream.Collectors.toList;


/**
 *
 * Ideas for Gatherers:
 * https://openjdk.org/jeps/473
 * https://www.reddit.com/r/java/comments/14le6tw/gatherers/
 *
 * https://www.youtube.com/watch?v=8fMFa6OqlY8
 *
 * Nikolai explains what it is https://www.youtube.com/watch?v=epgJm2dZTSg
 * Nikolai shows how to use it https://www.youtube.com/watch?v=pNQ5OXMXDbY
 *
 */
public class GathererTest {

    // static method that returns a Gatherer, this Gatherer implements a stream map
    public static <T, R> Gatherer<T, ?, R> map(Function<? super T, ? extends R> mapper) {
        return Gatherer.of( (unused, element, downstream) -> downstream.push(mapper.apply(element)) );
    }

    @Test
    public void testGathererAsMapper() {

        // Stream::gather(Gatherer) is to intermediate operations what Stream::collect(Collector) is to terminal operations
        // Gatherers can transform elements in a one-to-one, one-to-many, many-to-one, or many-to-many fashion

        // here we can reimplement simple mapping with a gatherer that is one-to-one
        var words = List.of("here", "is", "a", "word", "list");
        words.stream()
                .gather(map(String::toUpperCase))
                .forEach(System.out::println);
    }

    @Test
    public void testGathererAsSmoother() {

        // sliding window can be used for things like smoothing functions

        var trueVales = IntStream.range(1, 11)
                .mapToDouble(i -> i)
                .boxed()
                .toList();

        var noisyValues = trueVales.stream()
                .map(i -> i + (Math.random()-0.5))
                .toList();

        var smoothedValues = noisyValues.stream()
                .gather(Gatherers.windowSliding(5))
                .map(window -> window.stream().flatMapToDouble(DoubleStream::of).average().orElseThrow())
                .toList();


        noisyValues.forEach(System.out::println);

        System.out.println("Smoothed values");

        smoothedValues.forEach(System.out::println);

    }



    // We can transform a Collector to a Gatherer that emits the result downstream,
    // transforming terminal operations into intermediate operations
    public static <T,A,R> Gatherer<T,A,R> fromCollector(Collector<T,A,R> collector) {

        Supplier<A> supplier = collector.supplier();
        BiConsumer<A,T> accumulator = collector.accumulator();
        BinaryOperator<A> combiner = collector.combiner();
        Function<A,R> finisher = collector.finisher();

        return Gatherer.of(
                supplier, // initializer
                (state, element, downstream) -> {
                    accumulator.accept(state, element);
                    return true; // integrator returns false if no subsequent integration is desired
                    },
                combiner, // combiner
                (state, downstream) -> downstream.push(finisher.apply(state)) // finisher
        );
    }

    @Test
    public void testGatherersFromCollectors() {


        var words = List.of("here", "is", "a", "word", "list");

        // these seem to reduce to the same thing

        words.stream()
                .gather(fromCollector(toList()))
                .forEach(System.out::println);

        words.stream()
                .collect(Collectors.toList())
                .stream()
                .forEach(System.out::println);


        // how is this different from simply using a collector and then .stream() ?
        // advantage is not having to create a new stream?
        // maybe this is more useful for other collectors?


    }
}
