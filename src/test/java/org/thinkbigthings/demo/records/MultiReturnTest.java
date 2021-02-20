package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.thinkbigthings.demo.records.Expression.*;

public class MultiReturnTest {

    @Test
    public void testMultipleReturnValues() {

        // of course we can compute this inline
        // but records give us the option to bundle logic together
        // and be more likely to pass around bundles of data
        Statistics s = compute(1, 2, 3);
        assertEquals(3, s.count());


        // native code often modifies arguments and uses return codes for errors
        // we can quickly and easily align our code more closely with native methods
        // handle return codes in switches, etc
        // of course we can wrap and throw exceptions
        // but records can lead us to bundle data together in ways we might not have done before

        MultiReturn<String> nativeError = pretendJniError();
        MultiReturn<String> nativeValue = pretendJniWorks();

        assertTrue(nativeValue.returnValue().isPresent());
        assertFalse(nativeError.errorCode() == 0);

    }

    record MultiReturn<T>(Optional<T> returnValue, Integer errorCode) {}

    public MultiReturn<String> pretendJniWorks() {
        return new MultiReturn<>(Optional.of("Native value here"), 0);
    }

    public MultiReturn<String> pretendJniError() {
        return new MultiReturn<>(Optional.empty(), 100);
    }



    record Statistics(int[] values, int sum, long count, double mean) {}

    Statistics compute(int... values) {
        long count = values.length;
        int sum = IntStream.of(values).reduce(0, Integer::sum);
        double mean = ((double)sum)/count;
        return new Statistics(values, sum, count, mean);
    }
}
