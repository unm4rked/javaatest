package com.example;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing a Car.
 */
public class Car {
    private String brand;
    private String model;
    private String type; // SUV, Sedan, Truck, etc.
    private Map<String, Double> prices;
    private Date releaseDate;
    private String preferredCurrency;

    public Car(String brand, String type, double price, LocalDate releaseDate, String currency) {
        this.brand = brand;
        this.type = type;
        this.prices = new HashMap<>();
        this.prices.put(currency, price);
        // Convert LocalDate to Date for compatibility
        this.releaseDate = java.sql.Date.valueOf(releaseDate);
        this.preferredCurrency = currency;
        this.model = ""; // Default empty model
    }
    
    public Car(String brand, String model, String type, Map<String, Double> prices) {
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.prices = prices;
        this.releaseDate = new Date(); // Default to current date
        this.preferredCurrency = prices.keySet().iterator().next(); // Default to first currency
    }
    
    public Car(String brand, String model, String type, Map<String, Double> prices, Date releaseDate) {
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.prices = prices;
        this.releaseDate = releaseDate;
        this.preferredCurrency = prices.keySet().iterator().next(); // Default to first currency
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

    public double getPrice() {
        return prices.getOrDefault(preferredCurrency, 0.0);
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

    public String getCurrency() {
        return preferredCurrency;
    }
    
    public String getPreferredCurrency() {
        return preferredCurrency;
    }
    
    public void setPreferredCurrency(String currency) {
        if (prices.containsKey(currency)) {
            this.preferredCurrency = currency;
        }
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", type='" + type + '\'' +
                ", price=" + getPrice() +
                ", releaseDate=" + releaseDate +
                ", currency='" + preferredCurrency + '\'' +
                '}';
    }
}
