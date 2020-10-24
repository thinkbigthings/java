package org.thinkbigthings.demo.records;

import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;

public class RecordTest {

    @Test
    public void testRecordSerialization() throws Exception {

        record Point3D(float x, float y, float z) implements Serializable {}

        Point3D userInfo = new Point3D(1, 2, 3);

        String serializedRecord = Paths.get("build", "serial.data").toString();
        try(var oos = new ObjectOutputStream(new FileOutputStream(serializedRecord))) {
            oos.writeObject(userInfo);
        }
        try(var ois = new ObjectInputStream(new FileInputStream(serializedRecord))) {
            System.out.println(ois.readObject());
        }

    }
}
