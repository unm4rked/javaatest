package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A simple Java application that demonstrates basic Java functionality using a GUI.
 */
public class App {
    private static JTextArea outputArea;
    
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Java Demo Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());
        
        // Create a text area for output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        
        // Add buttons for each demo
        JButton basicButton = new JButton("Basic Variables");
        JButton conditionalButton = new JButton("Conditional Demo");
        JButton arrayButton = new JButton("Array Demo");
        JButton loopButton = new JButton("Enhanced Loop Demo");
        JButton methodButton = new JButton("Method Call Demo");
        JButton objectButton = new JButton("Object Demo");
        JButton clearButton = new JButton("Clear Output");
        
        // Add action listeners
        basicButton.addActionListener(e -> demonstrateBasicVariables());
        conditionalButton.addActionListener(e -> demonstrateConditional());
        arrayButton.addActionListener(e -> demonstrateArray());
        loopButton.addActionListener(e -> demonstrateEnhancedLoop());
        methodButton.addActionListener(e -> demonstrateMethodCall());
        objectButton.addActionListener(e -> demonstrateObjectCreation());
        clearButton.addActionListener(e -> outputArea.setText(""));
        
        // Add buttons to panel
        buttonPanel.add(basicButton);
        buttonPanel.add(conditionalButton);
        buttonPanel.add(arrayButton);
        buttonPanel.add(loopButton);
        buttonPanel.add(methodButton);
        buttonPanel.add(objectButton);
        buttonPanel.add(clearButton);
        
        // Add button panel to frame
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        // Display the frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Initial welcome message
        appendToOutput("Hello, Java World!\n");
        appendToOutput("Click the buttons below to demonstrate Java features.\n");
    }
    
    private static void appendToOutput(String text) {
        outputArea.append(text);
        // Scroll to the bottom
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    private static void demonstrateBasicVariables() {
        // 1. Variable declaration and initialization
        int number = 42;
        String message = "Welcome to Java programming!";
        
        appendToOutput("\n--- Basic Variables Demo ---\n");
        appendToOutput("Integer variable: " + number + "\n");
        appendToOutput("String variable: " + message + "\n");
    }
    
    private static void demonstrateConditional() {
        // 2. Conditional statement
        int number = 42;
        
        appendToOutput("\n--- Conditional Demo ---\n");
        appendToOutput("Testing if " + number + " is greater than 40:\n");
        
        if (number > 40) {
            appendToOutput("The number is greater than 40.\n");
        } else {
            appendToOutput("The number is 40 or less.\n");
        }
    }
    
    private static void demonstrateArray() {
        // 3. Array creation and iteration
        String[] programmingLanguages = {"Java", "Python", "JavaScript", "C++", "Go"};
        
        appendToOutput("\n--- Array Demo ---\n");
        appendToOutput("Popular programming languages:\n");
        
        for (int i = 0; i < programmingLanguages.length; i++) {
            appendToOutput((i + 1) + ". " + programmingLanguages[i] + "\n");
        }
    }
    
    private static void demonstrateEnhancedLoop() {
        // 4. Enhanced for loop
        String[] programmingLanguages = {"Java", "Python", "JavaScript", "C++", "Go"};
        
        appendToOutput("\n--- Enhanced For Loop Demo ---\n");
        appendToOutput("Using enhanced for loop:\n");
        
        for (String language : programmingLanguages) {
            appendToOutput("- " + language + "\n");
        }
    }
    
    private static void demonstrateMethodCall() {
        // 5. Method call
        int sum = addNumbers(5, 7);
        
        appendToOutput("\n--- Method Call Demo ---\n");
        appendToOutput("Sum of 5 and 7 is: " + sum + "\n");
    }
    
    private static void demonstrateObjectCreation() {
        // 6. Object creation
        Person person = new Person("John", 30);
        
        appendToOutput("\n--- Object Creation Demo ---\n");
        appendToOutput("Person details: " + person + "\n");
    }
    
    /**
     * Adds two numbers and returns the result.
     * 
     * @param a first number
     * @param b second number
     * @return sum of the two numbers
     */
    private static int addNumbers(int a, int b) {
        return a + b;
    }
}

/**
 * A simple class representing a person with name and age.
 */
class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}