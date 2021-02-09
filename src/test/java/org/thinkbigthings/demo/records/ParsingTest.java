package org.thinkbigthings.demo.records;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParsingTest {

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

        assertThrows(ValueInstantiationException.class, () -> mapper.readValue(json, ShortWord.class));

    }


    @Test
    public void testGson() throws Exception {

        String json = """
                {
                    "firstName": "Bilbo",
                    "lastName": "Baggins"
                }
                """;

        record Person(@Expose String firstName, @Expose String lastName) {
            public Person() {
                this("", "");
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Can't use GSON, see https://github.com/google/gson/issues/1794
        Person person = gson.fromJson(json, Person.class);
        String output = gson.toJson(new Person("Bilbo", "Baggins"));

        class PersonDeserializer implements JsonDeserializer<Person> {
            @Override
            public Person deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                String s = json.getAsString();
                throw new RuntimeException("Not implemented");
            }
        }

        class PersonInstanceCreator implements InstanceCreator<Person> {
            public Person createInstance(Type type) {
                return new Person("", "");
            }
        }

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Person.class, new PersonDeserializer())
                .registerTypeAdapter(Person.class, new PersonInstanceCreator())
                .create();

        person = gson.fromJson(json, Person.class);
        output = gson.toJson(new Person("Bilbo", "Baggins"));

        assertEquals("Bilbo", person.firstName());
        assertEquals("Baggins", person.lastName());
    }

}
