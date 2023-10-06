package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MapKeyTest {


    @Test
    public void testBadMultiKeyMap() {

        // TODO Describe how equals and hashcode are used in the implementation of HashMap
        Map<CustomMapKey, Date> employeeStartDates = new HashMap<>();

        CustomMapKey key1 = new CustomMapKey("123", "4567");
        CustomMapKey key2 = new CustomMapKey("1234", "567");

        // TODO Why are these the same value?
        int h1 = key1.hashCode();
        int h2 = key2.hashCode();

        employeeStartDates.put(key1, Date.from(Instant.now()));
        employeeStartDates.put(key2, Date.from(Instant.now()));

        assertNotNull(employeeStartDates.get(key1));

        // TODO why did the hashcode change? Is this an acceptable thing to do?
        key1.setCompany("1234");
        h1 = key1.hashCode();

        assertEquals(2, employeeStartDates.size());

        // TODO the map knows key1 is there... why are we getting null? How could we fix this?
        assertNull(employeeStartDates.get(key1));

    }

    @Test
    public void testMultiKeyMap() {

        // the key for a map must be immutable (have a consistent hashcode)
        // so records are a good fit
        record EmployeeKey(String companyId, String employeeId) {}

        Map<EmployeeKey, Date> employeeStartDates = new HashMap<>();

        EmployeeKey k1 = new EmployeeKey("123", "4567");
        EmployeeKey k2 = new EmployeeKey("1234", "567");
        Date d1 = Date.from(Instant.now());

        int h1 = k1.hashCode();
        int h2 = k2.hashCode();

        employeeStartDates.put(k1, d1);
        employeeStartDates.put(k2, Date.from(Instant.now()));

        assertEquals(2, employeeStartDates.size());

        assertEquals(d1, employeeStartDates.get(k1));

        assertEquals(d1, employeeStartDates.get(new EmployeeKey("123", "4567")));

    }


    public static class CustomMapKey {

        private String company, employee;

        public CustomMapKey(String company, String employee) {
            this.company = company;
            this.employee = employee;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getEmployee() {
            return employee;
        }

        public void setEmployee(String employee) {
            this.employee = employee;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CustomMapKey that = (CustomMapKey) o;
            return company.equals(that.company) && employee.equals(that.employee);
        }

        @Override
        public int hashCode() {
            return (company + employee).hashCode();
        }
    }

}
