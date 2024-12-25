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
import static java.util.stream.IntStream.iterate;
import static org.thinkbigthings.demo.gatherers.FunctionalFinders.toExactlyOne;


/**
 *
 * Ideas for Gatherers:
 * https://openjdk.org/jeps/473
 * https://www.reddit.com/r/java/comments/14le6tw/gatherers/
 * https://www.reddit.com/r/java/comments/1fyzynb/stream_gatherers_jep_485/
 *
 * https://cr.openjdk.org/~vklang/Gatherers.html
 *
 * https://www.youtube.com/watch?v=8fMFa6OqlY8  (implementations pick up at 21:45)
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

    // one of the upcoming gatherer libraries has a rolling average gatherer, I think
    private Optional<Double> average(List<Double> window) {
        return window.stream().flatMapToDouble(DoubleStream::of).average().stream().boxed().findFirst();
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
                .flatMap(Optional::stream)
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
    }

    @Test
    public void testHistogram() {

        class Histogram {

            private final int binSize;
            private final NavigableMap<Integer, Integer> map;

            public Histogram(int binSize) {
                this.binSize = binSize;
                this.map = new TreeMap<>();
            }

            public Histogram putValue(Integer value) {
                int bin = value - (value % binSize);
                map.put(bin, map.getOrDefault(bin, 0) + 1);
                return this;
            }

            public NavigableMap<Integer, Integer> map() {
                setUnfilledBins();
                return map;
            }

            private void setUnfilledBins() {
                int minBin = map.firstKey();
                int maxBin = map.lastKey();
                IntStream.iterate(minBin, b -> b <= maxBin, b -> b + binSize).forEach(b -> map.putIfAbsent(b,0));
            }
        }

        // construct a set of histogram bins for a given set of data
        // the values map to a single element - the Histogram - which can then be extracted from the stream
        var hist = Stream.of(1,2,3,4,5,4,5,6,6,6,7,7,8,9,13)
                .gather(fold(() -> new Histogram(2), Histogram::putValue))
                .collect(toExactlyOne());

        System.out.println(hist.map());
    }


    record Range(int min, int max) { }

    // not thread safe, should only be used with a sequential stream
    static class RangeGatheringState {

        private int min = Integer.MAX_VALUE;
        private int max = Integer.MIN_VALUE;
        private boolean hasGatheredValues = false;

        public boolean integrate(Integer value) {
            min = Math.min(min, value);
            max = Math.max(max, value);
            hasGatheredValues = true;
            return true;
        }

        public Optional<Range> range() {
            return hasGatheredValues ? Optional.of(new Range(min, max)) : Optional.empty();
        }
    }

    private Function<Range, Stream<Integer>> toHistogramBins(int binSize) {
        return range -> {
            int lowestBin =  (range.min() / binSize) * binSize;
            return iterate(lowestBin, b -> b <= range.max(), b -> b + binSize).boxed();
        };
    }

    @Test
    public void testMinMax() {

        // why not pass an instance of these classes to the gatherer instead of these generators and functions?
        // this allows us to create multiple gatherers for the same stream if it is parallelized
        // the runtime will create a new instance of the state for each thread

        final int binSize = 2;

        // if there are no elements found, the finisher is still called
        // downstream is not used by the integrator if you need to see all elements before you can continue the stream
        // integrator returns false if it wants to short circuit (e.g. Stream.limit())
        Gatherer<Integer, RangeGatheringState, Integer> histogramBins = Gatherer.ofSequential(
                RangeGatheringState::new,
                (state, element, _) -> state.integrate(element),
                (state, downstream) -> state.range()
                        .map(toHistogramBins(binSize))
                        .orElse(Stream.empty())
                        .forEach(downstream::push)
        );

        // see if we can construct a set of histogram bins for a given set of data
        var hist = Stream.of(1,2,3,4,5,4,5,6,6,6,7,7,8,9,13)
                .gather(histogramBins)
                .toList();

        System.out.println(hist);

        // TODO turn these into unit tests, and test case of empty inputs
//        hist = IntStream.of().boxed()
//                .gather(minMax)
//                .findFirst();
//
//        System.out.println(hist);


//        Stream.of(1,2,3,4,5,4,5,6,6,6,7,7,8,9,13)
//                .gather(minMax)
//                .findFirst();
//        var bins = hist.map(range -> histogramBinsFromRange(range,3))
//                .orElse(Stream.empty())
//                .toList();
//
//        System.out.println(bins);


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
