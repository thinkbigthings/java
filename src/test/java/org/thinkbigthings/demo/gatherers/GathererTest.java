package org.thinkbigthings.demo.gatherers;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Gatherers.fold;
import static java.util.stream.Gatherers.scan;


/**
 *
 * Ideas for Gatherers:
 * https://openjdk.org/jeps/473
 * https://www.reddit.com/r/java/comments/14le6tw/gatherers/
 * https://www.reddit.com/r/java/comments/1fyzynb/stream_gatherers_jep_485/
 *
 * https://cr.openjdk.org/~vklang/Gatherers.html
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

    private double average(List<Double> window) {
        return window.stream().flatMapToDouble(DoubleStream::of).average().orElseThrow();
    }

    @Test
    public void testGathererSlidingWindow() {

        // sliding window can be used for things like smoothing functions
        // there are better curve-fitting algorithms, but this illustrates the idea

        var xValues = IntStream.range(1, 62)
                .mapToDouble(i -> i / 10d)
                .boxed()
                .toList();

        var yValues = xValues.stream()
                .map(Math::sin)
                .toList();

        var noisyValues = yValues.stream()
                .map(i -> i + ((0.4 * Math.random()) - 0.2))
                .toList();

        var smoothedValues = noisyValues.stream()
                .gather(Gatherers.windowSliding(5))
                .map(this::average)
                .toList();


        System.out.println("X Values");
        xValues.forEach(System.out::println);

        System.out.println("Noisy Values");
        noisyValues.forEach(System.out::println);

        System.out.println("Smoothed values");
        smoothedValues.forEach(System.out::println);


    }

    @Test
    public void testFold() {

        // looking at the source code for fold() and scan() in Gatherers.java is helpful


        //  reduce is a kind of fold. Reduction takes a stream and turns it into a single value.
        //  Folding also does this, but it loosens the requirements:
        //  1) that the return type is of the same type as the stream elements
        //  2) that the combiner is associative
        //  3) the initializer on fold is a generator function not a static value.
//        System.out.println("Fold example: Fibonacci sequence");
//        Stream.of(1, 2, 3, 4, 5)
//                .gather(Gatherers.fold(() -> 1, (a, b) -> a * b))
//                .forEach(System.out::println);


        var freqMap = Arrays.stream("gatherer".split(""))
                .gather(fold(HashMap<String, Integer>::new, (map, str) -> {
                    // since we're using fold, we can mutate the map in place
                    map.put(str, map.getOrDefault(str, 0) + 1);
                    return map;
                }))
                .findFirst() // since we only get one element out of fold, we can just grab the first one
                .orElseGet(HashMap::new);

        System.out.println(freqMap);

        record Histogram(NavigableMap<Integer, Integer> map, int binSize) {

            public Histogram(int binSize) {
                this(new TreeMap<>(), binSize);
            }

            public Histogram putValue(Integer value) {
                int bin = map.floorKey(value);
//                int bin =  (value / binSize) * binSize; // get the lowest bin that is a multiple of binSize
                map.put(bin, map.getOrDefault(bin, 0) + 1);
                return this;
            }

            public Histogram setUnfilledBins() {
                int minBin = map.firstKey();
                int maxBin = map.lastKey();
                IntStream.iterate(minBin, b -> b <= maxBin, b -> b + binSize).forEach(b -> map.putIfAbsent(b,0));
                return this;
            }
        }

        // see if we can construct a set of histogram bins for a given set of data
        var hist = Stream.of(1,2,3,4,5,4,5,6,6,6,7,7,8,9,13)
                .gather(fold(() -> new Histogram(2), Histogram::putValue))
                .map(Histogram::setUnfilledBins)
                .toList();

        System.out.println(hist);
    }

    record Range(int min, int max) { }

    static class RangeGatheringState {

        private int min = Integer.MAX_VALUE;
        private int max = Integer.MIN_VALUE;

        public boolean integrate(Integer value) {
            min = Math.min(min, value);
            max = Math.max(max, value);
            return true;
        }

        public boolean hasGatheredValues() {
            return ! (min == Integer.MAX_VALUE && max == Integer.MIN_VALUE);
        }

        public Optional<Range> range() {
            return hasGatheredValues() ? Optional.of(new Range(min, max)) : Optional.empty();
        }
    }

    @Test
    public void testMinMax() {

        // why not pass an instance of these classes to the gatherer instead of generators and functions?
        // this allows us to use create multiple gatherers for the same stream if it is parallelized
        // the system will create a new instance of the state for each thread

        // if there are no elements found, the finisher is still called
        // downstream is not used by the integrator if you need to see all elements before you can continue the stream
        // integrator returns false if it wants to short circuit
        Gatherer<Integer, RangeGatheringState, Range> minMax = Gatherer.ofSequential(
                RangeGatheringState::new,
                (state, element, downstream) -> state.integrate(element),
                (state, downstream) -> state.range().ifPresent(downstream::push)
        );

        // see if we can construct a set of histogram bins for a given set of data
        var hist = Stream.of(1,2,3,4,5,4,5,6,6,6,7,7,8,9,13)
                .gather(minMax)
                .toList();

        System.out.println(hist);

        hist = IntStream.of().boxed()
                .gather(minMax)
                .toList();

        System.out.println(hist);

    }

    @Test
    public void testGatherScan() {

//        List<String> numberStrings = Stream.of(1,2,3,4,5,6,7,8,9)
//                .gather(scan(() -> "", (string, number) -> string + number) )
//                .toList();
//
//        System.out.println(numberStrings);

        // create a frequency map
        // the BiFunction could be extracted for readability
        var freqMap = Arrays.stream("gatherer".split(""))
                .gather(scan(HashMap<String, Integer>::new, (map, str) -> {
                    // Create a new map based on the previous map to avoid mutating the original one
                    // each resulting map is emitted separately
                    HashMap<String, Integer> newMap = new HashMap<>(map);
                    newMap.put(str, newMap.getOrDefault(str, 0) + 1);
                    return newMap;
                }))
                .toList();

        System.out.println(freqMap);
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
                .collect(toList())
                .stream()
                .forEach(System.out::println);


        // how is this different from simply using a collector and then .stream() ?
        // advantage is not having to create a new stream?
        // maybe this is more useful for other collectors or for other gatherers?
        // the gatherer can short circuit the stream if it wants to, but a collector MUST consume the whole stream
        // the gatherer MIGHT consume the whole stream, but it doesn't HAVE to, depending on the use case.
        // if it always consumes the whole stream, then yes it acts the same as a collector


    }
}
