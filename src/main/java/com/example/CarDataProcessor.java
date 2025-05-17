package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A car data processor application that can parse and process both XML and CSV files,
 * then display their contents according to specific criteria.
 */
public class CarDataProcessor extends JFrame {
    private JComboBox<String> filterTypeComboBox;
    private JTextField filterValueField;
    private JTextField filterBrandField;
    private JTextField filterDateField;
    private JComboBox<String> sortTypeComboBox;
    private JComboBox<String> outputFormatComboBox;
    private JCheckBox currencyFilterCheckBox;
    private JTextArea resultArea;
    private JTable resultTable;
    private JScrollPane scrollPane;
    private JLabel filterValueLabel;
    private JLabel filterBrandLabel;
    private JLabel filterDateLabel;
    
    private List<Car> cars = new ArrayList<>();
    private Map<String, Date> brandReleaseDates = new HashMap<>();
    
    public CarDataProcessor() {
        setTitle("Car Data Processor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create control panel
        JPanel controlPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Filter options
        controlPanel.add(new JLabel("Filter By:"));
        filterTypeComboBox = new JComboBox<>(new String[]{"None", "Brand", "Brand and Price", "Brand and Release Date"});
        filterTypeComboBox.addActionListener(e -> updateFilterForm());
        controlPanel.add(filterTypeComboBox);
        
        filterValueLabel = new JLabel("Filter Value:");
        controlPanel.add(filterValueLabel);
        filterValueField = new JTextField();
        controlPanel.add(filterValueField);
        
        filterBrandLabel = new JLabel("Brand:");
        controlPanel.add(filterBrandLabel);
        filterBrandField = new JTextField();
        controlPanel.add(filterBrandField);
        
        filterDateLabel = new JLabel("Filter Date (yyyy-MM-dd):");
        controlPanel.add(filterDateLabel);
        filterDateField = new JTextField();
        controlPanel.add(filterDateField);
        
        // Initially set up form based on default filter type
        updateFilterForm();
        
        // Sort options
        controlPanel.add(new JLabel("Sort By:"));
        sortTypeComboBox = new JComboBox<>(new String[]{"None", "Latest to Oldest", "Highest Price to Lowest"});
        controlPanel.add(sortTypeComboBox);
        
        // Output format options
        controlPanel.add(new JLabel("Output Format:"));
        outputFormatComboBox = new JComboBox<>(new String[]{"Table", "XML", "JSON"});
        controlPanel.add(outputFormatComboBox);
        
        // Currency filter option
        controlPanel.add(new JLabel("Apply Currency Filter:"));
        currencyFilterCheckBox = new JCheckBox("SUV in EUR, Sedan in JPY, Truck in USD");
        controlPanel.add(currencyFilterCheckBox);
        
        // Add control panel to the top
        add(controlPanel, BorderLayout.NORTH);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton processButton = new JButton("Process Data");
        processButton.addActionListener(e -> processData());
        buttonPanel.add(processButton);
        
        JButton loadDataButton = new JButton("Load Data");
        loadDataButton.addActionListener(e -> loadData());
        buttonPanel.add(loadDataButton);
        
        // Add button panel below the control panel
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Create result area (initially empty)
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultTable = new JTable();
        scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Set visibility
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void loadData() {
        try {
            // Load CSV data
            loadCSVData();
            
            // Load XML data
            loadXMLData();
            
            JOptionPane.showMessageDialog(this, "Data loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
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
            String brand = getBrandFromModel(model); // Assuming model can be used to determine brand
            
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
    
    private String getElementTextContent(Element parent, String tagName) {
        NodeList elements = parent.getElementsByTagName(tagName);
        if (elements.getLength() > 0) {
            return elements.item(0).getTextContent();
        }
        return "";
    }
    
    private String getBrandFromModel(String model) {
        // This is a simplified mapping - in a real application, you would have a more comprehensive mapping
        Map<String, String> modelToBrand = new HashMap<>();
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
        
        return modelToBrand.getOrDefault(model, "Unknown");
    }
    
    private void processData() {
        if (cars.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please load data first!", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Apply filters
            List<Car> filteredCars = filterCars();
            
            // Apply sorting
            sortCars(filteredCars);
            
            // Apply currency filter if selected
            if (currencyFilterCheckBox.isSelected()) {
                applyCurrencyFilter(filteredCars);
            }
            
            // Display results in selected format
            displayResults(filteredCars);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private List<Car> filterCars() throws ParseException {
        List<Car> filteredCars = new ArrayList<>(cars);
        String filterType = (String) filterTypeComboBox.getSelectedItem();
        String filterValue = filterValueField.getText().trim();
        String brandValue = filterBrandField.getText().trim();
        
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
            String dateStr = filterDateField.getText().trim();
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
    
    private void updateFilterForm() {
        String filterType = (String) filterTypeComboBox.getSelectedItem();
        
        // Reset fields
        filterValueField.setText("");
        filterBrandField.setText("");
        filterDateField.setText("");
        
        // Update form based on filter type
        if (filterType == null || filterType.equals("None")) {
            filterValueLabel.setVisible(false);
            filterValueField.setVisible(false);
            filterBrandLabel.setVisible(false);
            filterBrandField.setVisible(false);
            filterDateLabel.setVisible(false);
            filterDateField.setVisible(false);
        } else if (filterType.equals("Brand")) {
            filterValueLabel.setText("Brand:");
            filterValueLabel.setVisible(true);
            filterValueField.setVisible(true);
            filterBrandLabel.setVisible(false);
            filterBrandField.setVisible(false);
            filterDateLabel.setVisible(false);
            filterDateField.setVisible(false);
            filterValueField.setToolTipText("Enter brand name");
        } else if (filterType.equals("Brand and Price")) {
            filterValueLabel.setText("Price:");
            filterValueLabel.setVisible(true);
            filterValueField.setVisible(true);
            filterBrandLabel.setVisible(true);
            filterBrandField.setVisible(true);
            filterDateLabel.setVisible(false);
            filterDateField.setVisible(false);
            filterValueField.setToolTipText("Enter minimum price value");
            filterBrandField.setToolTipText("Enter brand name");
        } else if (filterType.equals("Brand and Release Date")) {
            filterValueLabel.setText("Brand:");
            filterValueLabel.setVisible(true);
            filterValueField.setVisible(true);
            filterBrandLabel.setVisible(false);
            filterBrandField.setVisible(false);
            filterDateLabel.setVisible(true);
            filterDateField.setVisible(true);
            filterValueField.setToolTipText("Enter brand name");
            filterDateField.setToolTipText("Enter date in yyyy-MM-dd format");
        }
        
        // Refresh the panel
        revalidate();
        repaint();
    }
    
    private void sortCars(List<Car> carList) {
        String sortType = (String) sortTypeComboBox.getSelectedItem();
        
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
    
    private void applyCurrencyFilter(List<Car> carList) {
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
    
    // Helper method to determine type order for sorting
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
    
    private void displayResults(List<Car> carList) {
        String outputFormat = (String) outputFormatComboBox.getSelectedItem();
        
        if (outputFormat == null) {
            outputFormat = "Table";
        }
        
        // Remove current component from center
        remove(scrollPane);
        
        if (outputFormat.equals("Table")) {
            displayTableFormat(carList);
        } else if (outputFormat.equals("XML")) {
            displayXmlFormat(carList);
        } else if (outputFormat.equals("JSON")) {
            displayJsonFormat(carList);
        }
        
        // Refresh the UI
        revalidate();
        repaint();
    }
    
    private void displayTableFormat(List<Car> carList) {
        // Create table model with columns
        String[] columns = {"Brand", "Model", "Type", "Price", "Currency", "Release Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // Add rows to table
        for (Car car : carList) {
            String currency = car.getPreferredCurrency();
            double price = car.getPrices().getOrDefault(currency, 0.0);
            
            Object[] row = {
                car.getBrand(),
                car.getModel(),
                car.getType(),
                String.format("%.2f", price),
                currency,
                dateFormat.format(car.getReleaseDate())
            };
            
            model.addRow(row);
        }
        
        // Set model to table and add to scroll pane
        resultTable = new JTable(model);
        scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void displayXmlFormat(List<Car> carList) {
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
            
            xml.append("    <price currency=\"").append(currency).append("\">");
            xml.append(String.format("%.2f", price)).append("</price>\n");
            xml.append("    <releaseDate>").append(dateFormat.format(car.getReleaseDate())).append("</releaseDate>\n");
            xml.append("  </car>\n");
        }
        
        xml.append("</cars>");
        
        // Display XML in text area
        resultArea = new JTextArea(xml.toString());
        resultArea.setEditable(false);
        scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void displayJsonFormat(List<Car> carList) {
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
        
        // Display JSON in text area
        resultArea = new JTextArea(json.toString());
        resultArea.setEditable(false);
        scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create application
        SwingUtilities.invokeLater(() -> new CarDataProcessor());
    }
    
    /**
     * Car class to store car information
     */
    private static class Car {
        private String brand;
        private String model;
        private String type;
        private Map<String, Double> prices;
        private Date releaseDate;
        private String preferredCurrency = "USD"; // Default currency
        
        public Car(String brand, String model, String type, Map<String, Double> prices) {
            this.brand = brand;
            this.model = model;
            this.type = type;
            this.prices = prices;
            this.releaseDate = new Date(); // Default to current date
        }
        
        public String getBrand() {
            return brand;
        }
        
        public String getModel() {
            return model;
        }
        
        public String getType() {
            return type;
        }
        
        public Map<String, Double> getPrices() {
            return prices;
        }
        
        public Date getReleaseDate() {
            return releaseDate;
        }
        
        public void setReleaseDate(Date releaseDate) {
            this.releaseDate = releaseDate;
        }
        
        public String getPreferredCurrency() {
            return preferredCurrency;
        }
        
        public void setPreferredCurrency(String preferredCurrency) {
            this.preferredCurrency = preferredCurrency;
        }
    }
}