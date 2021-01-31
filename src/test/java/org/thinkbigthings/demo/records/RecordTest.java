package org.thinkbigthings.demo.records;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    public void testJackson() throws Exception {

        String json = """
                {
                    "firstName": "Bilbo",
                    "lastName": "Baggins"
                }
                """;

        record Person(String firstName, String lastName) {}

        Person person = new ObjectMapper().readValue(json, Person.class);

        assertEquals("Bilbo", person.firstName());
        assertEquals("Baggins", person.lastName());
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

        var input  = new ObjectInputStream( new FileInputStream(serializedFile));
        var output = new ObjectOutputStream(new FileOutputStream(serializedFile));

        try(input; output) {

            var point = new Point(1, 2);
            output.writeObject(point);

            var point2 = input.readObject();

            assertEquals(point, point2);
        }
    }

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
}
