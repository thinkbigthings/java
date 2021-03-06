package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.thinkbigthings.demo.records.Functional.uncheck;
import static org.thinkbigthings.demo.records.Try.*;

public class StreamTest {

    @Test
    public void testRetainStreamData() {

        var words = List.of("here", "is", "a", "word", "list");

        // map the data and pass along with original value

        // using "var" here allows us to assign to a real type (not Object, but anonymous)
        // this is allowed via LVTI introduced in Java 10, but it's... cumbersome
        var longWords = words.stream()
                .map(element -> new Object() {
                    String word = element;
                    int length = element.length();
                    Instant processedTimestamp = Instant.now();
                })
                .filter(t -> t.length > 3)
                .collect(toList());

//        longWords.iterator().next().
        System.out.println(longWords);

        // using an explicit type during processing makes the same code more readable
        // and we have an explicit type at the end which is easier to reason about
        record ProcessedWord(String word, int length, Instant recorded) {}

        var longWords2 = words.stream()
                .map(word -> new ProcessedWord(word, word.length(), Instant.now()))
                .filter(word -> word.length() > 3)
                .collect(toList());

//        longWords2.iterator().next().
        System.out.println(longWords2);

    }

    @Test
    public void testTargetTerminalOperation() {

        // People sometimes use Map.Entry to handle simple pairs, including inside Streams.
        // Records make this much more usable.

        record Charge(double amount, double tax) {
            public Charge add(Charge other){
                return new Charge(amount + other.amount, tax + other.tax);
            }
        }

        List<Charge> itemizedCharges = List.of(
                new Charge(2,1),
                new Charge(3,1),
                new Charge(4,1));

        // one idea to get the totals, but not clear later that these values are intended to go together
        double totalAmount = itemizedCharges.stream()
                .map(Charge::amount)
                .reduce(0.0, Double::sum);

        double totalTax = itemizedCharges.stream()
                .map(Charge::tax)
                .reduce(0.0, Double::sum);

        // another way to get the totals
        // records can be a good target for reduce operations
        Charge total = itemizedCharges.stream()
                .reduce(new Charge(0,0), Charge::add);

        // records make a good merger for teeing operations
        Charge total2 = itemizedCharges.stream()
                .collect(teeing(summingDouble(Charge::amount), summingDouble(Charge::tax), Charge::new));

    }

    @Test
    public void testStreamExceptions() {


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        // which one won't be parsed? use this in your next coding interview
        List<String> dates = List.of("2021-06-21", "whoops", "2001-12-21");

        // sub-optimal approach...

        // code reviewers hate this one weird trick
        try {
            dates.stream()
                    .map(uncheck(format::parse))
                    .collect(toList());
        }
        catch(Exception e) {
            e.printStackTrace();
        }


        // another approach

        // use the Try object to attempt every element in the stream
        // and save the exceptions and results to separate lists

        var tries = dates.stream()
                .map(tryCatch(format::parse))
                .collect(toList());

        // TODO wait until midnight and send this to someone's pager
        var exceptions = tries.stream()
                .map(Try::exception)
                .filter(Objects::nonNull)
                .collect(toList());

        var successes = tries.stream()
                .map(Try::result)
                .filter(Objects::nonNull)
                .collect(toList());


        // better approach

        record ResultsAndExceptions<R>(List<R> results, List<? extends Exception> exceptions) {}

        // records make a great merger for teeing operations
        ResultsAndExceptions<Date> results = dates.stream()
                .map(tryCatch(format::parse))
                .collect(teeing( toResults(), toExceptions(), ResultsAndExceptions::new));

        assertEquals(2, results.results().size());
        assertEquals(1, results.exceptions().size());

    }
}
