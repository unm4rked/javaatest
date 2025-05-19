package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    
    // Service for handling data processing logic
    private CarDataService carDataService;
    
    public CarDataProcessor() {
        setTitle("Car Data Processor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize the service
        carDataService = new CarDataService();
        
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
            // Use the service to load data
            carDataService.loadData();
            
            JOptionPane.showMessageDialog(this, "Data loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void processData() {
        if (carDataService.getCars().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please load data first!", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get filter parameters
            String filterType = (String) filterTypeComboBox.getSelectedItem();
            String filterValue = filterValueField.getText().trim();
            String brandValue = filterBrandField.getText().trim();
            String dateStr = filterDateField.getText().trim();
            
            // Apply filters using the service
            java.util.List<Car> filteredCars = carDataService.filterCars(filterType, filterValue, brandValue, dateStr);
            
            // Apply sorting using the service
            String sortType = (String) sortTypeComboBox.getSelectedItem();
            carDataService.sortCars(filteredCars, sortType);
            
            // Apply currency filter if selected
            if (currencyFilterCheckBox.isSelected()) {
                carDataService.applyCurrencyFilter(filteredCars);
            }
            
            // Display results in selected format
            displayResults(filteredCars);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
    
    private void displayResults(java.util.List<Car> carList) {
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
    
    private void displayTableFormat(java.util.List<Car> carList) {
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
    
    private void displayXmlFormat(java.util.List<Car> carList) {
        // Use the service to format as XML
        String xml = carDataService.formatAsXml(carList);
        
        // Display XML in text area
        resultArea = new JTextArea(xml);
        resultArea.setEditable(false);
        scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void displayJsonFormat(java.util.List<Car> carList) {
        // Use the service to format as JSON
        String json = carDataService.formatAsJson(carList);
        
        // Display JSON in text area
        resultArea = new JTextArea(json);
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
}