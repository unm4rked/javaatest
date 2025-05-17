package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the App class.
 */
public class AppTest {
    
    @Test
    public void testAddNumbers() {
        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = App.class.getDeclaredMethod("addNumbers", int.class, int.class);
            method.setAccessible(true);
            
            // Test cases
            assertEquals(12, method.invoke(null, 5, 7), "5 + 7 should equal 12");
            assertEquals(0, method.invoke(null, 0, 0), "0 + 0 should equal 0");
            assertEquals(-3, method.invoke(null, -1, -2), "-1 + -2 should equal -3");
            assertEquals(100, method.invoke(null, 50, 50), "50 + 50 should equal 100");
            
        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        }
    }
    
    @Test
    public void testPersonToString() {
        // Create a new Person instance using reflection
        try {
            Class<?> personClass = Class.forName("com.example.Person");
            java.lang.reflect.Constructor<?> constructor = personClass.getDeclaredConstructor(String.class, int.class);
            Object person = constructor.newInstance("Alice", 25);
            
            // Test the toString method
            String expected = "Person{name='Alice', age=25}";
            assertEquals(expected, person.toString(), "Person toString should match expected format");
            
        } catch (Exception e) {
            fail("Exception occurred during test: " + e.getMessage());
        }
    }
}