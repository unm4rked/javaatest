package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 * Class to parse, filter, sort, and output car data from XML and CSV files.
 */
public class CarProcessor {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy,dd,MM");

    /**
     * Parses car data from an XML file.
     * @param filePath path to the XML file
     * @return list of Car objects
     * @throws Exception if parsing fails
     */
    public List<Car> parseXml(String filePath) throws Exception {
        List<Car> cars = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(Files.newInputStream(Paths.get(filePath)));
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("car");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element carElement = (Element) nodeList.item(i);
            String brand = carElement.getElementsByTagName("brand").item(0).getTextContent();
            String type = carElement.getElementsByTagName("type").item(0).getTextContent();
            double price = Double.parseDouble(carElement.getElementsByTagName("price").item(0).getTextContent());
            String releaseDateStr = carElement.getElementsByTagName("releaseDate").item(0).getTextContent();
            LocalDate releaseDate = LocalDate.parse(releaseDateStr, DATE_FORMATTER);
            String currency = carElement.getElementsByTagName("currency").item(0).getTextContent();

            cars.add(new Car(brand, type, price, releaseDate, currency));
        }
        return cars;
    }

    /**
     * Parses car data from a CSV file.
     * Expected CSV format: brand,type,price,releaseDate,currency
     * releaseDate format: yyyy,dd,MM
     * @param filePath path to the CSV file
     * @return list of Car objects
     * @throws IOException if reading file fails
     */
    public List<Car> parseCsv(String filePath) throws IOException {
        List<Car> cars = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 7) continue;
                String brand = parts[0].trim();
                String type = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                // releaseDate is split into 3 parts yyyy,dd,mm
                int year = Integer.parseInt(parts[3].trim());
                int day = Integer.parseInt(parts[4].trim());
                int month = Integer.parseInt(parts[5].trim());
                LocalDate releaseDate = LocalDate.of(year, month, day);
                String currency = parts[6].trim();

                cars.add(new Car(brand, type, price, releaseDate, currency));
            }
        }
        return cars;
    }

    /**
     * Filters cars by brand and price.
     * @param cars list of cars
     * @param brand brand to filter by
     * @param maxPrice maximum price
     * @return filtered list of cars
     */
    public List<Car> filterByBrandAndPrice(List<Car> cars, String brand, double maxPrice) {
        return cars.stream()
                .filter(car -> car.getBrand().equalsIgnoreCase(brand) && car.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * Filters cars by brand and release date.
     * @param cars list of cars
     * @param brand brand to filter by
     * @param releaseDate release date to filter by
     * @return filtered list of cars
     */
    public List<Car> filterByBrandAndReleaseDate(List<Car> cars, String brand, LocalDate releaseDate) {
        return cars.stream()
                .filter(car -> car.getBrand().equalsIgnoreCase(brand) && car.getReleaseDate().equals(releaseDate))
                .collect(Collectors.toList());
    }

    /**
     * Sorts cars by release date (latest to oldest).
     * @param cars list of cars
     * @return sorted list of cars
     */
    public List<Car> sortByReleaseDateDesc(List<Car> cars) {
        return cars.stream()
                .sorted(Comparator.comparing(Car::getReleaseDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Sorts cars by price (highest to lowest).
     * @param cars list of cars
     * @return sorted list of cars
     */
    public List<Car> sortByPriceDesc(List<Car> cars) {
        return cars.stream()
                .sorted(Comparator.comparing(Car::getPrice).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Outputs car data in table format.
     * @param cars list of cars
     * @return string representing table format
     */
    public String outputTableFormat(List<Car> cars) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s %-10s %-10s %-15s %-10s%n", "Brand", "Type", "Price", "Release Date", "Currency"));
        sb.append("-------------------------------------------------------------\n");
        for (Car car : cars) {
            sb.append(String.format("%-10s %-10s %-10.2f %-15s %-10s%n",
                    car.getBrand(), car.getType(), car.getPrice(), car.getReleaseDate(), car.getCurrency()));
        }
        return sb.toString();
    }

    /**
     * Outputs car data in XML format.
     * @param cars list of cars
     * @return string representing XML format
     */
    public String outputXmlFormat(List<Car> cars) {
        StringBuilder sb = new StringBuilder();
        sb.append("<cars>\n");
        for (Car car : cars) {
            sb.append("  <car>\n");
            sb.append("    <brand>").append(car.getBrand()).append("</brand>\n");
            sb.append("    <type>").append(car.getType()).append("</type>\n");
            sb.append("    <price>").append(car.getPrice()).append("</price>\n");
            sb.append("    <releaseDate>").append(car.getReleaseDate()).append("</releaseDate>\n");
            sb.append("    <currency>").append(car.getCurrency()).append("</currency>\n");
            sb.append("  </car>\n");
        }
        sb.append("</cars>");
        return sb.toString();
    }

    /**
     * Outputs car data in JSON format.
     * @param cars list of cars
     * @return string representing JSON format
     */
    public String outputJsonFormat(List<Car> cars) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            sb.append("  {\n");
            sb.append("    \"brand\": \"").append(car.getBrand()).append("\",\n");
            sb.append("    \"type\": \"").append(car.getType()).append("\",\n");
            sb.append("    \"price\": ").append(car.getPrice()).append(",\n");
            sb.append("    \"releaseDate\": \"").append(car.getReleaseDate()).append("\",\n");
            sb.append("    \"currency\": \"").append(car.getCurrency()).append("\"\n");
            sb.append("  }");
            if (i < cars.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Optional: Sort all SUV in EUR, all Sedan in JPY, all Truck in USD.
     * This method assumes prices are already in the respective currencies.
     * @param cars list of cars
     * @return sorted list of cars by type and price descending
     */
    public List<Car> sortByTypeAndCurrencyPrice(List<Car> cars) {
        return cars.stream()
                .sorted(Comparator.comparing(Car::getType)
                        .thenComparing(Comparator.comparing(Car::getPrice).reversed()))
                .collect(Collectors.toList());
    }

}
