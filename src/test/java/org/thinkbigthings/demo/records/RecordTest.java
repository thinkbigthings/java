package org.thinkbigthings.demo.records;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RecordTest {

    @Test
    public void basicRecords() {

        // fields are called "record components"
        record MinMax(int min, int max) { }
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

        // does not re-use instances like String... Maybe later?
        MinMax m2 = new MinMax(0, 0);

        assertEquals(m, m2);
    }


    @Test
    public void testValidatingConstructor() {

        // this should throw IllegalArgumentException if you try to construct something with both sides
        assertThrows(IllegalArgumentException.class, () -> new Try<>(new RuntimeException(), ""));
        assertThrows(IllegalArgumentException.class, () -> new Try<>(null, null));
    }

    @Test
    public void testCustomBuilder() {

        BuildablePerson me = BuildablePerson.newPerson().withFirstName("Me");

        BuildablePerson you = me.withFirstName("You");

        assertEquals(me.firstName(), "Me");
        assertEquals(you.firstName(), "You");
        assertNotEquals(me, you);
    }

    @Test
    @DisplayName("Record serialization")
    public void testSerialization() throws Exception {

        File serializedFile = Paths.get("build", "serial.data").toFile();

        // records still have to implement Serializable to participate in serialization
        record Point(float x, float y) implements Serializable {}


        Point p1 = new Point(1, 2);

        try(var output = new ObjectOutputStream(new FileOutputStream(serializedFile))) {
            output.writeObject(p1);
        }

        try(var input  = new ObjectInputStream(new FileInputStream(serializedFile))) {
            Point p2 = (Point)input.readObject();
            assertEquals(p1, p2);
        }

    }

//    public interface StoreRepository extends JpaRepository<Store, Long> {
//
//        Optional<Store> findByName(String name);
//
//        @Query("SELECT new org.thinkbigthings.zdd.dto.StoreRecord" +
//                "(s.name, s.website) " +
//                "FROM Store s " +
//                "ORDER BY s.name ASC ")
//        Page<StoreRecord> loadSummaries(Pageable page);
//    }

    @Test
    public void testMultiKeyMap() {

        // the key for a map must be immutable (have a consistent hashcode)
        // so records are a good fit
        record EmployeeKey(int companyId, int employeeId) {}

        Map<EmployeeKey, Date> employeeStartDates = new HashMap<>();

        employeeStartDates.put(new EmployeeKey(123, 4567), Date.from(Instant.now()));
        employeeStartDates.put(new EmployeeKey(1234, 567), Date.from(Instant.now()));

        assertEquals(2, employeeStartDates.size());
    }

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

        MultiReturn<String> nativeError = pretendNativeError();
        MultiReturn<String> nativeValue = pretendNativeWorks();

        assertTrue(nativeValue.returnValue().isPresent());
        assertFalse(nativeError.errorCode() == 0);

    }

    record MultiReturn<T>(Optional<T> returnValue, Integer errorCode) {}

    public MultiReturn<String> pretendNativeWorks() {
        return new MultiReturn<>(Optional.of("Native value here"), 0);
    }

    public MultiReturn<String> pretendNativeError() {
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
