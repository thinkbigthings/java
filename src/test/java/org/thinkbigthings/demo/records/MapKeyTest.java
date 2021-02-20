package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapKeyTest {

    @Test
    public void testMultiKeyMap() {

        // the key for a map must be immutable (have a consistent hashcode)
        // so records are a good fit
        record EmployeeKey(int companyId, int employeeId) {}

        Map<EmployeeKey, Date> employeeStartDates = new HashMap<>();

        employeeStartDates.put(new EmployeeKey(123, 4567), Date.from(Instant.now()));
        employeeStartDates.put(new EmployeeKey(1234, 567), Date.from(Instant.now()));

        assertEquals(2, employeeStartDates.size());
    }

}
