package org.thinkbigthings.demo.records;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static java.util.stream.Stream.ofNullable;
import static org.thinkbigthings.demo.records.Functional.uncheck;
import static org.thinkbigthings.demo.records.Try.*;

public class DemoRecords {

    public static void main(String[] args) {


        //////////////////////////////////////////////////////////////////////////
        // BASICS

        // fields are called "record components"
        record MinMax(int min, int max) {  }
        record Range(int min, int max) { }

        // can't assign a Range to a MinMax or cast to it (we don't have structural typing)
        // would need to define an interface that matches the generated methods

        // If we define it twice in two different methods in same class,
        // it just gets compiled to distinct inner classes.

        // records can take other records as arguments
        record Range2D(Range x, Range y) { }

        // can't use static on a class defined inside a method
        // nonstatic inner classes risk carrying around a reference to the enclosing class and impacting garbage


        // reference to inner class has nowhere to hide!
        MinMax m = new MinMax(0, 0);

        // does not re-use instances like String... Maybe later? Could always add Record.intern()
        MinMax m2 = new MinMax(0, 0);


        /////////////////////////////////////////////////////////////////////////////////////////
        // PAIRS AND STREAMS

        // People sometimes use Map.Entry to handle simple pairs, including inside Streams.
        // Records make this much more usable.
        // e.e. https://stackoverflow.com/questions/58229186/how-to-aggregate-multiple-fields-using-collectors-in-java

        record Charge(double amount, double tax) {
            public Charge add(Charge other){
                return new Charge(amount + other.amount, tax + other.tax);
            }
        }

        List<Charge> itemizedCharges = List.of(
                new Charge(1,2),
                new Charge(3,4),
                new Charge(5,6));

        // one idea to get the totals
        double totalAmount = itemizedCharges.stream()
                .map(Charge::amount)
                .reduce(0.0, Double::sum);

        double totalTax = itemizedCharges.stream()
                .map(Charge::tax)
                .reduce(0.0, Double::sum);


        // this is probably the most readable, at the cost of adding a new method to Charge.
        // Doesn't require any records or cryptic stream techniques
        // The data is the same but the MEANING might be a little different (total vs itemized)
        Charge total = itemizedCharges.stream()
                .reduce(new Charge(0,0), Charge::add);


        // Yes, this is the same data structure as Charge, but this new TYPE indicates the MEANING not just the data.
        // The cost of duplicate data structure is trivial enough that we can do this because why not.
        record Total(double amount, double tax){}

        // FYI Teeing lets us reduce to a type that's different from the elements being reduced
        Total total2 = itemizedCharges.stream()
                .collect(teeing(summingDouble(Charge::amount), summingDouble(Charge::tax), Total::new));


        ///////////////////////////////////////////////////////////////////////
        // STREAMS AND EXCEPTIONS

        List<String> names = new ArrayList<>();
        names.add("a");
        names.add("b");
        names.add(null);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        List<String> dates = List.of("2021-06-21", "whoops", "2001-12-21");

        // start with simple uncheck
        try {

            List<Date> parsedDates = dates.stream()
                    .map(uncheck(format::parse))
                    .collect(toList());
        }
        catch(Exception e) {
            e.printStackTrace();
        }


        dates.stream()
                .map(uncheck(d -> format.parse(d)))
                .collect(toList());

        var tries = dates.stream()
                .map(tryCatch(d -> format.parse(d)))
                .collect(toList());

        var exceptions = tries.stream()
                .map(Try::exception)
                .filter(Objects::nonNull)
                .collect(toList());

        var successes = tries.stream()
                .map(Try::result)
                .filter(Objects::nonNull)
                .collect(toList());


        System.out.println(tries);
        System.out.println(exceptions);
        System.out.println(successes);


        // TODO want to "catch" the exceptions that occurred in the stream
        // or collectLeft() to assume you take an Either and extract left from it
        // or collectExceptions() to collect to a List and map the successful entries to their values and continue the stream
        // or unwrapTry() to collect exceptions to a list and return the possibly empty result in a stream... flatMap(t -> unwrapTry(t, () -> exceptions))

        // or should a try/catch process the exception immediately to a callback, forwarding only results,
        // eliminating need for Either or Try?
        // (modify withExceptionCapture)
        // pass an Exception consumer into the try/lift

        // exceptions in streams should be preserved and processed through a parallel pipe in the stream
        // custom collector that collects into multiple provided collections for left and right side
        // There's a way to process streams to different lists
        // See collectors .teeing(), partitioning by, and grouping by


        // this works, but we have to look back at the collector to remember what the Boolean means
        var attempts = names.stream()
                .map(tryCatch(String::length))
                .collect(partitioningBy(t -> t.exception() != null));

        // this is one way to multiple return values
        record Results<T>(List<? extends Exception> exceptions, List<T> counts) {}

//        Results<Integer> c1 = names.stream()
//                .map(tryCatch(String::length))
//                .collect(teeing( flatMapping(Try::exceptions, toList()), flatMapping(Try::results, toList()), Results::new));

        Results<Integer> c = names.stream()
                .map(tryCatch(String::length))
                .collect(teeing( toExceptions(), toResults(), Results::new));

        System.out.println(c.counts());
        System.out.println(c.exceptions());
    }

}