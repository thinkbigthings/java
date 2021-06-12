package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.Test;


import static org.thinkbigthings.demo.records.Expression.*;

import static org.junit.jupiter.api.Assertions.*;

public class TreeNodeTest {

    @Test
    public void testASTWithSealedTypes() {

        //      -
        //    /   \
        //   +     1
        //  / \
        // 5   4

        // (5+4) - 1 = 8
        Expression five = new IntExp(5);
        Expression four = new IntExp(4);
        Expression plus = new AddExp(five, four);
        Expression root = new SubtractExp(plus, new IntExp(1));

        assertEquals(8, root.value());
    }

}
