package com.example;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Demo class to manually run and demonstrate CarProcessor functionality with interactive prompts.
 */
public class CarProcessorDemo {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            CarProcessor processor = new CarProcessor();

            System.out.println("Select input file type (1=XML, 2=CSV):");
            int fileType = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter input file path:");
            String filePath = scanner.nextLine();

            List<Car> cars;
            if (fileType == 1) {
                cars = processor.parseXml(filePath);
            } else if (fileType == 2) {
                cars = processor.parseCsv(filePath);
            } else {
                System.out.println("Invalid file type selected.");
                return;
            }

            System.out.println("Select filter option:");
            System.out.println("1: Filter by Brand and Price");
            System.out.println("2: Filter by Brand and Release Date");
            System.out.println("0: No filter");
            int filterOption = Integer.parseInt(scanner.nextLine());

            if (filterOption == 1) {
                System.out.println("Enter brand:");
                String brand = scanner.nextLine();
                System.out.println("Enter max price:");
                double maxPrice = Double.parseDouble(scanner.nextLine());
                cars = processor.filterByBrandAndPrice(cars, brand, maxPrice);
            } else if (filterOption == 2) {
                System.out.println("Enter brand:");
                String brand = scanner.nextLine();
                System.out.println("Enter release date (yyyy-mm-dd):");
                String dateStr = scanner.nextLine();
                LocalDate releaseDate = LocalDate.parse(dateStr);
                cars = processor.filterByBrandAndReleaseDate(cars, brand, releaseDate);
            }

            System.out.println("Select sort option:");
            System.out.println("1: Sort by Release Date (latest to oldest)");
            System.out.println("2: Sort by Price (highest to lowest)");
            System.out.println("3: Optional sort by Type and Currency Price");
            System.out.println("0: No sort");
            int sortOption = Integer.parseInt(scanner.nextLine());

            if (sortOption == 1) {
                cars = processor.sortByReleaseDateDesc(cars);
            } else if (sortOption == 2) {
                cars = processor.sortByPriceDesc(cars);
            } else if (sortOption == 3) {
                cars = processor.sortByTypeAndCurrencyPrice(cars);
            }

            System.out.println("Select output format:");
            System.out.println("1: Table");
            System.out.println("2: XML");
            System.out.println("3: JSON");
            int outputOption = Integer.parseInt(scanner.nextLine());

            String output;
            if (outputOption == 1) {
                output = processor.outputTableFormat(cars);
            } else if (outputOption == 2) {
                output = processor.outputXmlFormat(cars);
            } else if (outputOption == 3) {
                output = processor.outputJsonFormat(cars);
            } else {
                System.out.println("Invalid output option.");
                return;
            }



            System.out.println("Output:");
            System.out.println(output);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
