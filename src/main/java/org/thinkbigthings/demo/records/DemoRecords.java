package org.thinkbigthings.demo.records;


import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import static java.util.Optional.*;
import static java.util.stream.Collectors.*;

public class DemoRecords {

    public static void main(String[] args) {

        List<String> names = new ArrayList<>();
        names.add("a");
        names.add("b");
        names.add(null);


        // TODO prove we can use it with CHECKED exceptions
        // TODO .lift() is kind of esoteric to Java programmers, maybe use .tryWith()

        List<Either<? extends Exception, Integer>> counts = names.stream()
                .map(Either.lift(String::length))
                .collect(toList());

        List<? extends Exception> exceptions = counts.stream()
                .flatMap(e -> e.streamLeft())
                .collect(toList());

        List<Integer> successes = counts.stream()
                .flatMap(e -> e.streamRight())
                .collect(toList());

        // TODO use improved stack trace feature as one way to handle exceptions

        System.out.println(counts);
        System.out.println(exceptions);
        System.out.println(successes);

        // java --enable-preview --source 15 DemoRecords.java
        // interesting functional reading: https://github.com/hemanth/functional-programming-jargon


        // TODO want to "catch" the exceptions that occurred in the stream
        // or collectLeft() to assume you take an Either and extract left from it
        // or collectExceptions() to collect to a List and map the successful entries to their values and continue the stream
        // or unwrapTry() to collect exceptions to a list and return the possibly empty result in a stream... flatMap(t -> unwrapTry(t, () -> exceptions))

        //

        // or should a try/catch process the exception immediately to a callback, forwarding only results, eliminating need for Either or Try?
        // (modify withExceptionCapture)
        // pass an Exception consumer into the try/lift

        // Venkat suggests that exceptions in streams should be preserved and processed through a parallel pipe in the stream
        // TODO custom collector that collects into multiple provided collections for left and right side?
        // There's a way to process streams to different lists
        // See collectors .teeing(), partitioning by, and grouping by

        // TODO contrast with var as intermediate type
        // https://github.com/thinkbigthings/java/blob/master/java-10/Main.java

        List<EitherRecord<? extends Exception, Integer>> counts2 = names.stream()
                .map(tryCatch(String::length))
                .collect(toList());

        List<? extends Exception> exceptions2 = counts2.stream()
                .flatMap(e -> e.streamLeft())
                .collect(toList());

        List<Integer> successes2 = counts2.stream()
                .flatMap(e -> e.streamRight())
                .collect(toList());


        System.out.println(counts2);
        System.out.println(exceptions2);
        System.out.println(successes2);


        List<Try<Integer>> counts3 = names.stream()
                .map(withTry(String::length))
                .collect(toList());

        List<? extends Exception> exceptions3 = counts3.stream()
                .flatMap(t -> t.streamException())
                .collect(toList());

        List<Integer> successes3 = counts3.stream()
                .flatMap(t -> t.streamResult())
                .collect(toList());


        System.out.println(counts3);
        System.out.println(exceptions3);
        System.out.println(successes3);



        // this works fine, but we have to look back at the collector to remember what the Boolean means
        Map<Boolean, List<EitherRecord>> attempts = names.stream()
                .map(tryCatch(String::length))
                .collect(partitioningBy(EitherRecord::hasLeft));

        // this is the closest we can get to multiple return values
        record CountResults(List<? extends Exception> exceptions, List<Integer> counts) {}

        CountResults c = names.stream()
                .map(withTry(String::length))
                .collect(teeing(flatMapping(Try::exceptions, toList()), flatMapping(Try::results, toList()), CountResults::new));

        c.counts();
        c.exceptions();


        // People sometimes use Map.Entry to collect teeing results, Records make this much more usable
        // e.e. https://stackoverflow.com/questions/58229186/how-to-aggregate-multiple-fields-using-collectors-in-java

        record Charge(double amount, double tax) {
            public static Charge add(Charge c1, Charge c2){
                return new Charge(c1.amount() + c2.amount(), c1.tax() + c2.tax());
            }
        }
        List<Charge> itemizedCharges = List.of(new Charge(1,2), new Charge(3,4), new Charge(5,6));

        // one person's idea to get the totals
        double totalAmount = itemizedCharges.stream().map(Charge::amount).reduce(0.0, Double::sum);
        double totalTax = itemizedCharges.stream().map(Charge::tax).reduce(0.0, Double::sum);


        // this is probably the most readable, at the cost of adding a new method to Charge.
        // Doesn't require any records or obscure stream techniques
        // The data is the same but the MEANING might be a little different (total vs itemized)
        Charge total = itemizedCharges.stream().reduce(new Charge(0,0), Charge::add);

        // this could be defined as a new record: TotalCharges to indicate the meaning not just the data.
        // teeing lets us essentially reduce to a type that's different from the elements being reduced
        record Total(double amount, double tax){}
        Total total2 = itemizedCharges.stream()
                .collect(teeing(summingDouble(Charge::amount), summingDouble(Charge::tax), Total::new));



        // use case: parsing protobuf

        // use case: making calls or a set of sequential calls and recording how far each went
        // making network calls, opening and reading files...
        // could demonstrate with a unit test that loads a file and interrupts it while reading




        // fields are called "record components"
        record MinMax(int min, int max) {  }
        record Range(int min, int max) { }

        // can't assign a Range to a MinMax or cast to it (we don't have structural typing)
        // would need to implement an interface that matches the generated methods

        // If we define it twice in two different methods in same class,
        // it just gets compiled to distinct inner classes.

        // records can take other records
        record Range2D(Range x, Range y) { }


        // static class TestInner {} // can't use static on a class defined inside a method
        // nonstatic inner classes aren't usually defined inside a method because... boilerplate? Less readable?

        // reference to inner class has nowhere to hide!
        MinMax m = new MinMax(0, 0);

        // does not re-use instances like String... that is a compiler feature. Locally caching is hard. maybe later?
        MinMax m2 = new MinMax(0, 0);

        Person me = Person.newPerson().withFirstName("Me");
        Person you = me.withFirstName("You");

        System.out.println(me);
        System.out.println(you);


        // this should throw IllegalArgumentException
        try {
            new Try(new RuntimeException(), "");
        }
        catch(IllegalArgumentException e) {
            System.out.println("Caught illegal argument as expected");
        }

    }

    // This is one possible Builder pattern
    // Good news it's an easy one-liner per method, no separate Builder class, immutable by default, no .build() at the end
    // Bad news is it's a lot of boilerplate so it's error prone, and creates new object per builder method call
    // There are a couple third party libraries trying to generate record builders...
    // https://github.com/Randgalt/record-builder
    // https://github.com/javahippie/jukebox
    public static record Person(String firstName, String lastName) {
        public Person() {
            this("", "");
        }
        public static Person newPerson() {
            return new Person();
        }
        public Person withFirstName(String newFirstName) {
            return new Person(newFirstName, lastName);
        }
        public Person withLastName(String newLastName) {
            return new Person(firstName, newLastName);
        }
    }

    // https://dzone.com/articles/exception-handling-in-java-streams

    record Try<R>(Exception exception, R result) {

        // "compact constructor" has the implied args
        // cannot override the canonical constructor
        public Try {
            if(allSameNullness(exception, result)) {
                throw new IllegalArgumentException("Must have exactly one argument null");
            }
        }

        // "canonical constructor" matches the state parameters
        // other constructors MUST delegate to it
        public Try(Exception e) {
            this(e, null);
        }
        public Try(R result) {
            this(null, result);
        }
        public static <R> Stream<? extends Exception> exceptions(Try<R> tryResult) {
            return ofNullable(tryResult.exception()).stream();
        }
        public static <R> Stream<R> results(Try<R> tryResult) {
            return ofNullable(tryResult.result()).stream();
        }
        public Stream<? extends Exception> streamException() {
            return ofNullable(exception).stream();
        }
        public Stream<R> streamResult() {
            return ofNullable(result).stream();
        }
        public static <R> boolean hasException(Try<R> tryResult) {
            return tryResult.exception() != null;
        }
        public boolean hasOwnException() {
            return hasException(this);
        }
        private static boolean allSameNullness(Object... args) {
            return Stream.of(args).allMatch(Objects::isNull) || Stream.of(args).allMatch(Objects::nonNull);
        }
    }

    record EitherRecord<E, T>(E left, T right) {

        public static <E, T> EitherRecord < E, T > Left(E e) {
            return new EitherRecord<E, T>(e, null);
        }
        public static <E, T> EitherRecord < E, T > Right(T t) {
            return new EitherRecord<E, T>(null, t);
        }
        public static <E, T> boolean hasLeft(EitherRecord<E,T> either) {
            return either.getLeft().isPresent();
        }
        public boolean hasException() {
            return getLeft().isPresent();
        }
        public Optional<E> getLeft () {
            return Optional.ofNullable(left());
        }
        public Optional<T> getRight () {
            return Optional.ofNullable(right());
        }
        public Stream<E> streamLeft () {
            return getLeft().stream();
        }
        public Stream<T> streamRight () {
            return getRight().stream();
        }
    }

    public static <T, R> Function<T, Try<R>> withTry(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return new Try(function.apply(t));
            } catch (Exception ex) {
                return new Try(ex);
            }
        };
    }

    public static <T, R> Function<T, EitherRecord<? extends Exception, R>> tryCatch(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return EitherRecord.Right(function.apply(t));
            } catch (Exception ex) {
                return EitherRecord.Left(ex);
            }
        };
    }

    public static class Either<L, R> {

        private final L left;
        private final R right;

        private Either(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public static <L, R> Either<L, R> Left(L value) {
            return new Either(value, null);
        }

        public static <L, R> Either<L, R> Right(R value) {
            return new Either(null, value);
        }

        public Optional<L> getLeft() {
            return Optional.ofNullable(left);
        }

        public Stream<L> streamLeft() {
            return getLeft().stream();
        }

        public Optional<R> getRight() {
            return Optional.ofNullable(right);
        }

        public Stream<R> streamRight() {
            return getRight().stream();
        }

        public boolean isLeft() {
            return left != null;
        }

        public boolean isRight() {
            return right != null;
        }

        public static <T, R> Function<T, Either<? extends Exception, R>> lift(CheckedFunction<T, R> function) {
            return t -> {
                try {
                    return Either.Right(function.apply(t));
                } catch (Exception ex) {
                    return Either.Left(ex);
                }
            };
        }

        public String toString() {
            if (isLeft()) {
                return "Left(" + left + ")";
            }
            return "Right(" + right + ")";
        }
    }

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