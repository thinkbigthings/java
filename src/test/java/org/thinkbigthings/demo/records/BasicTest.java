package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.thinkbigthings.demo.records.Expression.*;

public class BasicTest {


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

        record PositiveInt(int value) {

            public PositiveInt(int value) {
                this.value = 100;
            }

//            PositiveInt {
//                // weird, the spec says:
//                // To enforce the intended use of compact constructors,
//                // it became a compile-time error to assign to any of the instance fields in the constructor body
            // as of Java 15,
//                // it seems the only thing we can NOT do is assign to "this.value" in the compact constructor
//                if(value <= 0) {
//                    value = 0;
////                    throw new IllegalArgumentException("Value must be > 0");
//                }
//            }
        }

        PositiveInt p = new PositiveInt(-1);
        int val = p.value();
//        assertThrows(IllegalArgumentException.class, () -> new PositiveInt(-1));

        assertEquals(1, new PositiveInt(1).value());
    }

    @Test
    public void testImmutability() {

        // records are shallowly immutable
        record WordList(List<String> words) {

            // we can assign in an overridden constructor, but not in a compact constructor
//            public WordList(List<String> words) {
//                this.words = Collections.unmodifiableList(words);
//            }

            public WordList {
                words = Collections.unmodifiableList(words);
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
    public void testReflection() {

        record Reflectable(String component) {}

        assertTrue(Reflectable.class.isRecord());
        assertFalse(this.getClass().isRecord());

        assertEquals(1, Reflectable.class.getRecordComponents().length);
        assertEquals("component", Reflectable.class.getRecordComponents()[0].getName());

    }

}
