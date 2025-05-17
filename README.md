# Java Application Example

This is a simple Java application that demonstrates basic Java programming concepts and features. The project is structured using Maven for dependency management and build automation.

## Project Structure

```
java-app/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── example/
│   │               └── App.java
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── AppTest.java
└── pom.xml
```

## Features Demonstrated

The application demonstrates several core Java features:

1. Basic console output
2. Variable declaration and initialization
3. Conditional statements (if-else)
4. Arrays and iteration (for loops)
5. Enhanced for loops
6. Method definition and calling
7. Object-oriented programming with classes
8. Unit testing with JUnit 5

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Apache Maven 3.6 or higher (optional, for building from source)

## Building the Application

To build the application, run the following command in the project root directory:

```bash
mvn clean package
```

This will compile the code, run the tests, and create a JAR file in the `target` directory.

## Running the Application

### Option 1: Using the JAR file

After building, you can run the application using:

```bash
java -cp target/classes;json-simple-1.1.1.jar com.example.CarDataProcessor
```

On Unix-based systems (Linux/macOS), use colons instead of semicolons:

```bash
java -cp target/classes:json-simple-1.1.1.jar com.example.CarDataProcessor
```

### Option 2: Using Maven

Alternatively, you can run it directly with Maven:

```bash
mvn exec:java -Dexec.mainClass="com.example.CarDataProcessor"
```

### Option 3: Using the compiled classes directly

If you've already compiled the classes (without packaging):

```bash
java -cp "target/classes;json-simple-1.1.1.jar" com.example.CarDataProcessor
```

## Data Files

The application requires the following data files to be present in the project root directory:

- `CarsBrand.csv` - Contains car brand information
- `carsType.xml` - Contains car type and pricing information

## Application Features

The Car Data Processor application provides the following functionality:

1. Loading and processing car data from CSV and XML files
2. Filtering cars by various criteria (Brand, Brand and Price, Brand and Release Date)
3. Sorting cars by type with specific currency preferences:
   - SUVs displayed in EUR (shown first)
   - Sedans displayed in JPY (shown second)
   - Trucks displayed in USD (shown third)
4. Displaying results in different formats (Table, XML, JSON)

## Running the Tests

To run the tests only:

```bash
mvn test
```

## Next Steps

This project serves as a starting point for Java development. Here are some ideas to extend it:

1. Add more complex data structures (Lists, Maps, Sets)
2. Implement file I/O operations
3. Add exception handling examples
4. Create a simple REST API using Spring Boot
5. Add a database connection using JDBC or an ORM like Hibernate