package com.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for processing car data.
 * This class handles loading, filtering, sorting, and formatting car data.
 */
public class CarDataService {
    private List<Car> cars = new ArrayList<>();
    private Map<String, Date> brandReleaseDates = new HashMap<>();
    private Map<String, String> modelToBrand = new HashMap<>();
    
    public CarDataService() {
        initializeModelToBrandMap();
    }
    
    /**
     * Initialize the mapping from car models to brands
     */
    private void initializeModelToBrandMap() {
        modelToBrand.put("RAV4", "Toyota");
        modelToBrand.put("Civic", "Honda");
        modelToBrand.put("F-150", "Ford");
        modelToBrand.put("Model X", "Tesla");
        modelToBrand.put("X5", "BMW");
        modelToBrand.put("A4", "Audi");
        modelToBrand.put("Silverado", "Chevrolet");
        modelToBrand.put("C-Class", "Mercedes-Benz");
        modelToBrand.put("Altima", "Nissan");
        modelToBrand.put("Sonata", "Hyundai");
    }
    
    /**
     * Load data from CSV and XML files
     * 
     * @throws Exception if loading fails
     */
    public void loadData() throws Exception {
        loadCSVData();
        loadXMLData();
    }
    
    /**
     * Load brand release dates from CSV file
     * 
     * @throws Exception if loading fails
     */
    private void loadCSVData() throws Exception {
        brandReleaseDates.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        
        try (BufferedReader reader = new BufferedReader(new FileReader("CarsBrand.csv"))) {
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // Remove quotes and split by comma
                line = line.replace("\"", "");
                String[] parts = line.split(",");
                
                if (parts.length >= 2) {
                    String brand = parts[0].trim();
                    Date releaseDate = dateFormat.parse(parts[1].trim());
                    brandReleaseDates.put(brand, releaseDate);
                }
            }
        }
    }
    
    /**
     * Load car data from XML file
     * 
     * @throws Exception if loading fails
     */
    private void loadXMLData() throws Exception {
        cars.clear();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("carsType.xml"));
        
        NodeList carNodes = document.getElementsByTagName("car");
        
        for (int i = 0; i < carNodes.getLength(); i++) {
            Element carElement = (Element) carNodes.item(i);
            
            // Extract car details
            String type = getElementTextContent(carElement, "type");
            String model = getElementTextContent(carElement, "model");
            String brand = getBrandFromModel(model);
            
            // Get main price
            Element priceElement = (Element) carElement.getElementsByTagName("price").item(0);
            double price = Double.parseDouble(priceElement.getTextContent());
            String currency = priceElement.getAttribute("currency");
            
            // Get additional prices
            Map<String, Double> prices = new HashMap<>();
            prices.put(currency, price);
            
            Element pricesElement = (Element) carElement.getElementsByTagName("prices").item(0);
            if (pricesElement != null) {
                NodeList additionalPrices = pricesElement.getElementsByTagName("price");
                for (int j = 0; j < additionalPrices.getLength(); j++) {
                    Element additionalPrice = (Element) additionalPrices.item(j);
                    String additionalCurrency = additionalPrice.getAttribute("currency");
                    double additionalValue = Double.parseDouble(additionalPrice.getTextContent());
                    prices.put(additionalCurrency, additionalValue);
                }
            }
            
            // Create car object and add to list
            Car car = new Car(brand, model, type, prices);
            car.setReleaseDate(brandReleaseDates.getOrDefault(brand, new Date()));
            cars.add(car);
        }
    }
    
    /**
     * Get text content of an XML element
     * 
     * @param parent parent element
     * @param tagName tag name to find
     * @return text content or empty string if not found
     */
    private String getElementTextContent(Element parent, String tagName) {
        NodeList elements = parent.getElementsByTagName(tagName);
        if (elements.getLength() > 0) {
            return elements.item(0).getTextContent();
        }
        return "";
    }
    
    /**
     * Get brand from model using the mapping
     * 
     * @param model car model
     * @return corresponding brand or "Unknown"
     */
    private String getBrandFromModel(String model) {
        return modelToBrand.getOrDefault(model, "Unknown");
    }
    
    /**
     * Filter cars based on criteria
     * 
     * @param filterType type of filter to apply
     * @param filterValue brand or price value
     * @param brandValue brand value for combined filters
     * @param dateStr date string for date filters
     * @return filtered list of cars
     * @throws ParseException if date parsing fails
     */
    public List<Car> filterCars(String filterType, String filterValue, String brandValue, String dateStr) throws ParseException {
        List<Car> filteredCars = new ArrayList<>(cars);
        
        if (filterType == null || filterType.equals("None")) {
            return filteredCars;
        }
        
        if (filterType.equals("Brand")) {
            if (filterValue.isEmpty()) {
                return filteredCars;
            }
            filteredCars = filteredCars.stream()
                    .filter(car -> car.getBrand().equalsIgnoreCase(filterValue))
                    .collect(Collectors.toList());
        } else if (filterType.equals("Brand and Price")) {
            try {
                if (filterValue.isEmpty() || brandValue.isEmpty()) {
                    return filteredCars;
                }
                double price = Double.parseDouble(filterValue);
                
                filteredCars = filteredCars.stream()
                        .filter(car -> car.getBrand().equalsIgnoreCase(brandValue) && 
                                car.getPrices().values().stream().anyMatch(p -> p >= price))
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid price format", 0);
            }
        } else if (filterType.equals("Brand and Release Date")) {
            if (filterValue.isEmpty() || dateStr.isEmpty()) {
                return filteredCars;
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date filterDate = dateFormat.parse(dateStr);
                
                filteredCars = filteredCars.stream()
                        .filter(car -> car.getBrand().equalsIgnoreCase(filterValue) && 
                                car.getReleaseDate().compareTo(filterDate) >= 0)
                        .collect(Collectors.toList());
            } catch (ParseException e) {
                throw new ParseException("Invalid date format. Use yyyy-MM-dd", 0);
            }
        }
        
        return filteredCars;
    }
    
    /**
     * Sort cars by specified criteria
     * 
     * @param carList list of cars to sort
     * @param sortType type of sorting to apply
     */
    public void sortCars(List<Car> carList, String sortType) {
        if (sortType == null || sortType.equals("None")) {
            return;
        }
        
        if (sortType.equals("Latest to Oldest")) {
            carList.sort((car1, car2) -> car2.getReleaseDate().compareTo(car1.getReleaseDate()));
        } else if (sortType.equals("Highest Price to Lowest")) {
            carList.sort((car1, car2) -> {
                double maxPrice1 = car1.getPrices().values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
                double maxPrice2 = car2.getPrices().values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
                return Double.compare(maxPrice2, maxPrice1);
            });
        }
    }
    
    /**
     * Apply currency filter based on car type
     * 
     * @param carList list of cars to filter
     */
    public void applyCurrencyFilter(List<Car> carList) {
        // First set preferred currency based on car type
        for (Car car : carList) {
            if (car.getType().equalsIgnoreCase("SUV")) {
                car.setPreferredCurrency("EUR");
            } else if (car.getType().equalsIgnoreCase("Sedan")) {
                car.setPreferredCurrency("JPY");
            } else if (car.getType().equalsIgnoreCase("Truck")) {
                car.setPreferredCurrency("USD");
            }
        }
        
        // Sort the list by car type (SUV, Sedan, Truck)
        carList.sort((car1, car2) -> {
            // First sort by type
            String type1 = car1.getType().toLowerCase();
            String type2 = car2.getType().toLowerCase();
            
            // Define type order: SUV, Sedan, Truck
            int typeOrder1 = getTypeOrder(type1);
            int typeOrder2 = getTypeOrder(type2);
            
            return Integer.compare(typeOrder1, typeOrder2);
        });
    }
    
    /**
     * Helper method to determine type order for sorting
     * 
     * @param type car type
     * @return order value
     */
    private int getTypeOrder(String type) {
        if (type.equals("suv")) {
            return 1; // SUV first (EUR)
        } else if (type.equals("sedan")) {
            return 2; // Sedan second (JPY)
        } else if (type.equals("truck")) {
            return 3; // Truck third (USD)
        } else {
            return 4; // Other types last
        }
    }
    
    /**
     * Get the list of cars
     * 
     * @return list of cars
     */
    public List<Car> getCars() {
        return cars;
    }
    
    /**
     * Format car data as XML string
     * 
     * @param carList list of cars to format
     * @return XML string
     */
    public String formatAsXml(List<Car> carList) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<cars>\n");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Car car : carList) {
            xml.append("  <car>\n");
            xml.append("    <brand>").append(car.getBrand()).append("</brand>\n");
            xml.append("    <model>").append(car.getModel()).append("</model>\n");
            xml.append("    <type>").append(car.getType()).append("</type>\n");
            
            String currency = car.getPreferredCurrency();
            double price = car.getPrices().getOrDefault(currency, 0.0);
            
            xml.append("    <price currency=\"").append(currency).append("\">")
               .append(String.format("%.2f", price)).append("</price>\n");
            xml.append("    <releaseDate>").append(dateFormat.format(car.getReleaseDate())).append("</releaseDate>\n");
            xml.append("  </car>\n");
        }
        
        xml.append("</cars>");
        return xml.toString();
    }
    
    /**
     * Format car data as JSON string
     * 
     * @param carList list of cars to format
     * @return JSON string
     */
    public String formatAsJson(List<Car> carList) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"cars\": [\n");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 0; i < carList.size(); i++) {
            Car car = carList.get(i);
            String currency = car.getPreferredCurrency();
            double price = car.getPrices().getOrDefault(currency, 0.0);
            
            json.append("    {\n");
            json.append("      \"brand\": \"").append(car.getBrand()).append("\",\n");
            json.append("      \"model\": \"").append(car.getModel()).append("\",\n");
            json.append("      \"type\": \"").append(car.getType()).append("\",\n");
            json.append("      \"price\": {\n");
            json.append("        \"value\": ").append(String.format("%.2f", price)).append(",\n");
            json.append("        \"currency\": \"").append(currency).append("\"\n");
            json.append("      },\n");
            json.append("      \"releaseDate\": \"").append(dateFormat.format(car.getReleaseDate())).append("\"\n");
            json.append("    }").append(i < carList.size() - 1 ? "," : "").append("\n");
        }
        
        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }
}