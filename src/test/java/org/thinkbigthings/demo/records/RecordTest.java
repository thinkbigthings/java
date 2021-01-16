package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Paths;

public class RecordTest {

    @Test
    @DisplayName("Record serialization")
    public void testSerialization() throws Exception {

        File serializedFile = Paths.get("build", "serial.data").toFile();

        // effectively final resources declared outside try
        var input  = new ObjectInputStream( new FileInputStream(serializedFile));
        var output = new ObjectOutputStream(new FileOutputStream(serializedFile));

        try(input; output) {

            record Point(float x, float y) implements Serializable {}

            var point = new Point(1, 2);
            output.writeObject(point);

            var point2 = input.readObject();

            System.out.println(point2);
            System.out.println(point.equals(point2));
        }

    }
}
