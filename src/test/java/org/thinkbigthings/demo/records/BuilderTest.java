package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderTest {

    @Test
    public void testCustomBuilder() {

        BuildablePerson me = BuildablePerson.newPerson().withFirstName("Me");

        BuildablePerson you = me.withFirstName("You");

        assertEquals(me.firstName(), "Me");
        assertEquals(you.firstName(), "You");
        assertNotEquals(me, you);
    }

}
