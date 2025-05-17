package com.example;

import static org.junit.Assert.*;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for CarProcessor class.
 */
public class CarProcessorTest {

    private static final String TEST_XML_FILE = "test_cars.xml";
    private static final String TEST_CSV_FILE = "test_cars.csv";

    private static CarProcessor carProcessor = new CarProcessor();

    @BeforeClass
    public static void setup() throws Exception {
        // Create a sample XML file for testing
        String xmlContent = 
            "<cars>\n" +
            "  <car>\n" +
            "    <brand>Toyota</brand>\n" +
            "    <type>SUV</type>\n" +
            "    <price>30000</price>\n" +
            "    <releaseDate>2022,15,06</releaseDate>\n" +
            "    <currency>EUR</currency>\n" +
            "  </car>\n" +
            "  <car>\n" +
            "    <brand>Honda</brand>\n" +
            "    <type>Sedan</type>\n" +
            "    <price>25000</price>\n" +
            "    <releaseDate>2021,10,05</releaseDate>\n" +
            "    <currency>JPY</currency>\n" +
            "  </car>\n" +
            "</cars>";
        try (FileWriter writer = new FileWriter(TEST_XML_FILE)) {
            writer.write(xmlContent);
        }

        // Create a sample CSV file for testing
        String csvContent = "brand,type,price,releaseDate,currency\n" +
                            "Ford,Truck,40000,2020,20,04,USD\n" +
                            "BMW,SUV,45000,2023,01,01,EUR\n";
        try (FileWriter writer = new FileWriter(TEST_CSV_FILE)) {
            writer.write(csvContent);
        }
    }

    @AfterClass
    public static void cleanup() throws Exception {
        Files.deleteIfExists(Paths.get(TEST_XML_FILE));
        Files.deleteIfExists(Paths.get(TEST_CSV_FILE));
    }

    @Test
    public void testParseXml() throws Exception {
        List<Car> cars = carProcessor.parseXml(TEST_XML_FILE);
        assertEquals(2, cars.size());
        assertEquals("Toyota", cars.get(0).getBrand());
        assertEquals("SUV", cars.get(0).getType());
        assertEquals(30000, cars.get(0).getPrice(), 0.001);
        assertEquals(LocalDate.of(2022, 6, 15), cars.get(0).getReleaseDate());
        assertEquals("EUR", cars.get(0).getCurrency());
    }

    @Test
    public void testParseCsv() throws Exception {
        List<Car> cars = carProcessor.parseCsv(TEST_CSV_FILE);
        assertEquals(2, cars.size());
        assertEquals("Ford", cars.get(0).getBrand());
        assertEquals("Truck", cars.get(0).getType());
        assertEquals(40000, cars.get(0).getPrice(), 0.001);
        assertEquals(LocalDate.of(2020, 4, 20), cars.get(0).getReleaseDate());
        assertEquals("USD", cars.get(0).getCurrency());
    }

    @Test
    public void testFilterByBrandAndPrice() {
        List<Car> cars = List.of(
            new Car("Toyota", "SUV", 30000, LocalDate.of(2022, 6, 15), "EUR"),
            new Car("Toyota", "Sedan", 20000, LocalDate.of(2021, 5, 10), "JPY"),
            new Car("Honda", "SUV", 25000, LocalDate.of(2020, 4, 20), "USD")
        );
        List<Car> filtered = carProcessor.filterByBrandAndPrice(cars, "Toyota", 25000);
        assertEquals(1, filtered.size());
        assertEquals("Sedan", filtered.get(0).getType());
    }

    @Test
    public void testFilterByBrandAndReleaseDate() {
        List<Car> cars = List.of(
            new Car("Toyota", "SUV", 30000, LocalDate.of(2022, 6, 15), "EUR"),
            new Car("Toyota", "Sedan", 20000, LocalDate.of(2021, 5, 10), "JPY"),
            new Car("Honda", "SUV", 25000, LocalDate.of(2020, 4, 20), "USD")
        );
        List<Car> filtered = carProcessor.filterByBrandAndReleaseDate(cars, "Toyota", LocalDate.of(2021, 5, 10));
        assertEquals(1, filtered.size());
        assertEquals("Sedan", filtered.get(0).getType());
    }

    @Test
    public void testSortByReleaseDateDesc() {
        List<Car> cars = List.of(
            new Car("Toyota", "SUV", 30000, LocalDate.of(2020, 6, 15), "EUR"),
            new Car("Honda", "Sedan", 25000, LocalDate.of(2022, 5, 10), "JPY"),
            new Car("Ford", "Truck", 40000, LocalDate.of(2021, 4, 20), "USD")
        );
        List<Car> sorted = carProcessor.sortByReleaseDateDesc(cars);
        assertEquals(LocalDate.of(2022, 5, 10), sorted.get(0).getReleaseDate());
        assertEquals(LocalDate.of(2021, 4, 20), sorted.get(1).getReleaseDate());
        assertEquals(LocalDate.of(2020, 6, 15), sorted.get(2).getReleaseDate());
    }

    @Test
    public void testSortByPriceDesc() {
        List<Car> cars = List.of(
            new Car("Toyota", "SUV", 30000, LocalDate.of(2020, 6, 15), "EUR"),
            new Car("Honda", "Sedan", 25000, LocalDate.of(2022, 5, 10), "JPY"),
            new Car("Ford", "Truck", 40000, LocalDate.of(2021, 4, 20), "USD")
        );
        List<Car> sorted = carProcessor.sortByPriceDesc(cars);
        assertEquals(40000, sorted.get(0).getPrice(), 0.001);
        assertEquals(30000, sorted.get(1).getPrice(), 0.001);
        assertEquals(25000, sorted.get(2).getPrice(), 0.001);
    }

    @Test
    public void testOutputTableFormat() {
        List<Car> cars = List.of(
            new Car("Toyota", "SUV", 30000, LocalDate.of(2020, 6, 15), "EUR")
        );
        String table = carProcessor.outputTableFormat(cars);
        assertTrue(table.contains("Toyota"));
        assertTrue(table.contains("SUV"));
        assertTrue(table.contains("30000.00"));
        assertTrue(table.contains("2020-06-15"));
        assertTrue(table.contains("EUR"));
    }

    @Test
    public void testOutputXmlFormat() {
        List<Car> cars = List.of(
            new Car("Toyota", "SUV", 30000, LocalDate.of(2020, 6, 15), "EUR")
        );
        String xml = carProcessor.outputXmlFormat(cars);
        assertTrue(xml.contains("<brand>Toyota</brand>"));
        assertTrue(xml.contains("<type>SUV</type>"));
        assertTrue(xml.contains("<price>30000.0</price>"));
        assertTrue(xml.contains("<releaseDate>2020-06-15</releaseDate>"));
        assertTrue(xml.contains("<currency>EUR</currency>"));
    }
}
