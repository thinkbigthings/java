package org.thinkbigthings.demo.records;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class RecordTest {


    @Test
    public void testJacksonRecords() throws Exception {

        String json = """
                {
                    "firstName": "Bilbo",
                    "lastName": "Baggins"
                }
                """;

        record Person(String firstName, String lastName) {}

        Person person = new ObjectMapper().readValue(json, Person.class);

        assertNotNull(person);
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

        // effectively final resources may be declared outside try
        var input  = new ObjectInputStream( new FileInputStream(serializedFile));
        var output = new ObjectOutputStream(new FileOutputStream(serializedFile));

        record Point(float x, float y) implements Serializable {}

        try(input; output) {

            var point = new Point(1, 2);
            output.writeObject(point);

            var point2 = input.readObject();

            assertEquals(point, point2);
        }
    }
}
