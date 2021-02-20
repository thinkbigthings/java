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


//    @Test
//    public void testGson() throws Exception {
//
//        String json = """
//                {
//                    "firstName": "Bilbo",
//                    "lastName": "Baggins"
//                }
//                """;
//
//        record Person(@Expose String firstName, @Expose String lastName) {
//            public Person() {
//                this("", "");
//            }
//        }
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//        // Can't use GSON, see https://github.com/google/gson/issues/1794
//        Person person = gson.fromJson(json, Person.class);
//        String output = gson.toJson(new Person("Bilbo", "Baggins"));
//
//        class PersonDeserializer implements JsonDeserializer<Person> {
//            @Override
//            public Person deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//                String s = json.getAsString();
//                throw new RuntimeException("Not implemented");
//            }
//        }
//
//        class PersonInstanceCreator implements InstanceCreator<Person> {
//            public Person createInstance(Type type) {
//                return new Person("", "");
//            }
//        }
//
//        gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .registerTypeAdapter(Person.class, new PersonDeserializer())
//                .registerTypeAdapter(Person.class, new PersonInstanceCreator())
//                .create();
//
//        person = gson.fromJson(json, Person.class);
//        output = gson.toJson(new Person("Bilbo", "Baggins"));
//
//        assertEquals("Bilbo", person.firstName());
//        assertEquals("Baggins", person.lastName());
//    }


    // static class can't be defined inside method
    // non-static inner class implicitly refers to its non-serializable enclosing class so would fail serialization
    static class PointClass implements Serializable { public float x, y; }


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
            Point p2 = (Point) input.readObject();
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

            PointClass pc2 = (PointClass) input.readObject();

            assertEquals(pc1.x, pc2.x);
            assertEquals(pc1.y, pc2.y);
            assertNotEquals(pc1, pc2);
        }

    }
}
