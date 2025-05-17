package com.example;

import java.time.LocalDate;

/**
 * Model class representing a Car.
 */
public class Car {
    private String brand;
    private String type; // SUV, Sedan, Truck, etc.
    private double price;
    private LocalDate releaseDate;
    private String currency;

    public Car(String brand, String type, double price, LocalDate releaseDate, String currency) {
        this.brand = brand;
        this.type = type;
        this.price = price;
        this.releaseDate = releaseDate;
        this.currency = currency;
    }

    public String getBrand() {
        return brand;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", releaseDate=" + releaseDate +
                ", currency='" + currency + '\'' +
                '}';
    }
}
