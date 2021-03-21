package org.thinkbigthings.demo.records;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DtoTest {

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
    public void parseInvalidRecord() throws Exception {

        String json = """
                {
                    "word": "1234567890"
                }
                """;

        record ShortWord(String word) {
            ShortWord {
                if(word.length() > 3) {
                    throw new IllegalArgumentException("word is too long");
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();

        // get a ValueInstantiationException caused by IllegalArgumentException
        // compact constructor validation is called during json parsing
        assertThrows(ValueInstantiationException.class, () -> mapper.readValue(json, ShortWord.class));
    }

    // GSON has some issues, see https://github.com/google/gson/issues/1794
    // GSON doesn't work with inline records, so need record declaration here
    private static record Person(String firstName, String lastName) {  }

    @Test
    void testGson() {

        String parsableJson = """
                {
                    "firstName": "bilbo",
                    "lastName": "baggins"
                }
                """;

        JsonDeserializer<Person> personDeserializer = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
            JsonObject jObject = json.getAsJsonObject();
            String firstName = jObject.get("firstName").getAsString();
            String lastName = jObject.get("lastName").getAsString();
            return new Person(firstName, lastName);
        };

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Person.class, personDeserializer)
                .create();

        // Test serialize
        Person bilbo = new Person("bilbo", "baggins");
        final String personJson = gson.toJson(bilbo);
        assertEquals("{\"firstName\":\"bilbo\",\"lastName\":\"baggins\"}", personJson);

        // Test deserialize
        final Person parsedPerson = gson.fromJson(parsableJson, Person.class);
        assertEquals(bilbo, parsedPerson);
        assertNotSame(bilbo, parsedPerson);

        // test values
        assertEquals(bilbo.firstName(), parsedPerson.firstName());
        assertEquals(bilbo.lastName(), parsedPerson.lastName());

    }


    // static class can't be defined inside method
    // non-static inner class implicitly refers to its non-serializable enclosing class so would fail serialization
    static class PointClass implements Serializable {
        public float x, y;
        public PointClass() {}
        public  PointClass(float x, float y) {
            if(x < 0 || y < 0) {
                throw new IllegalArgumentException("points must be  > 0");
            }
        }
    }

    @Test
    @DisplayName("Record serialization")
    public void testSerialization() throws Exception {

        File serializedRecord = Paths.get("build", "serial.data").toFile();

        // records still have to implement Serializable to participate in serialization
        record Point(float x, float y) implements Serializable {
            Point {
                if(x < 0 || y < 0) {
                    throw new IllegalArgumentException("points must be  > 0");
                }
            }
        }

        Point p1 = new Point(1, 2);

        try(var output = new ObjectOutputStream(new FileOutputStream(serializedRecord))) {
            output.writeObject(p1);
        }

        try(var input  = new ObjectInputStream(new FileInputStream(serializedRecord))) {
            Point p2 = (Point) input.readObject();
            assertEquals(p1, p2);
        }


        //////////////////////////////////

        File serializedClass = Paths.get("build", "serialclass.data").toFile();

        PointClass pc1 = new PointClass(1, 2);


        try(var output = new ObjectOutputStream(new FileOutputStream(serializedClass))) {
            output.writeObject(pc1);
        }

        try(var input  = new ObjectInputStream(new FileInputStream(serializedClass))) {

            // validation in the constructor is not called
            // the bytes could be modified to create an "impossible" object
            PointClass pc2 = (PointClass) input.readObject();

            assertEquals(pc1.x, pc2.x);
            assertEquals(pc1.y, pc2.y);
            assertNotEquals(pc1, pc2);
        }

    }
}
