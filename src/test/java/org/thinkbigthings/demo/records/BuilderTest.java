package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderTest {

    @Test
    public void testCustomBuilder() {

        BuildablePerson bilbo = BuildablePerson.newPerson()
                .withFirstName("Bilbo")
                .withLastName("Baggins");

        BuildablePerson frodo = bilbo.withFirstName("Frodo");

        assertEquals(bilbo.firstName(), "Bilbo");
        assertEquals(frodo.firstName(), "Frodo");
        assertNotEquals(bilbo, frodo);
    }

}
