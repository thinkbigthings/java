package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class RecordTest {

    @Test
    public void testImmutability() {

        // records are shallowly immutable
        record WordList(List<String> words) {

            // we can assign in an overridden constructor, but not in a compact constructor
            public WordList(List<String> words) {
                this.words = Collections.unmodifiableList(words);
            }
        }

        ArrayList<String> mutableWords = new ArrayList<>();
        mutableWords.add("The");
        mutableWords.add("fox");
        mutableWords.add("jumped");

        WordList list = new WordList(mutableWords);

        assertThrows(UnsupportedOperationException.class, () -> list.words().clear());

    }

    @Test
    public void testDeclarations() {

        // We now have the ability to declare these locally:
        // local record classes, local enum classes, and local interfaces

        enum MyEnum { THIS, THAT, OTHER_THING }

        interface HasMyEnum {
            MyEnum thing();
        }

        record EnumWrapper(MyEnum thing) implements HasMyEnum {}

        HasMyEnum myInterface = new EnumWrapper(MyEnum.OTHER_THING);

        assertEquals(MyEnum.OTHER_THING, myInterface.thing());


        class MyInnerClass {

            // an inner class can declare a member that is a record class
            public EnumWrapper enumWrapper;

            // To accomplish this,
            // as of Java 16 an inner class can declare a member that is explicitly or implicitly static
//            public static String NAME = "name";

        }

    }


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
    public void testReflection() {

        record Reflectable(String component) {}

        assertTrue(Reflectable.class.isRecord());
        assertFalse(this.getClass().isRecord());

        assertEquals(1, Reflectable.class.getRecordComponents().length);
        assertEquals("component", Reflectable.class.getRecordComponents()[0].getName());

    }

    @Test
    public void testValidatingConstructor() {

        record PositiveInt(int value) {
            PositiveInt {
                // weird, the spec says:
                // To enforce the intended use of compact constructors,
                // it became a compile-time error to assign to any of the instance fields in the constructor body
                if(value <= 0) {
                    value = 0;
//                    throw new IllegalArgumentException("Value must be > 0");
                }
            }
        }

        PositiveInt p = new PositiveInt(-1);
        int val = p.value();
//        assertThrows(IllegalArgumentException.class, () -> new PositiveInt(-1));

        assertEquals(1, new PositiveInt(1).value());
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

        File serializedRecord = Paths.get("build", "serial.data").toFile();

        // records still have to implement Serializable to participate in serialization
        record Point(float x, float y) implements Serializable {}

        Point p1 = new Point(1, 2);

        try(var output = new ObjectOutputStream(new FileOutputStream(serializedRecord))) {
            output.writeObject(p1);
        }

        try(var input  = new ObjectInputStream(new FileInputStream(serializedRecord))) {
            Point p2 = (Point)input.readObject();
            assertEquals(p1, p2);
        }


        //////////////////////////////////

        File serializedClass = Paths.get("build", "serialclass.data").toFile();

        PointClass pc1 = new PointClass();
        pc1.x = 1;
        pc1.y = 2;

        try(var output = new ObjectOutputStream(new FileOutputStream(serializedClass))) {
            output.writeObject(pc1);
        }

        try(var input  = new ObjectInputStream(new FileInputStream(serializedClass))) {
            PointClass pc2 = (PointClass)input.readObject();
            assertNotEquals(pc1, pc2);
        }


    }

    // static class can't be defined inside method
    // non-static inner class in method refers to enclosing class which is not serializable, so serialization would fail
    static class PointClass implements Serializable { public float x, y; }

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
