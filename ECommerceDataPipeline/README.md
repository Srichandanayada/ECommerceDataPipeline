
# E-commerce Data Pipeline Using Java and MySQL

## Project Overview
This project processes e-commerce data stored in CSV files using Java and loads it into a MySQL database. It validates records, calculates order amounts, and uses SQL queries to analyze business data.

## Technologies Used
- Java
- MySQL
- SQL
- JDBC
- CSV Files

## Project Structure
```text
ECommerceDataPipeline/
├── data/
│   ├── customers.csv
│   ├── products.csv
│   ├── orders.csv
│   └── payments.csv
├── lib/
│   └── MySQL Connector JAR
├── Main.java
└── README.md
```

## Database Tables
1. Customers
2. Products
3. Orders
4. Payments

## Project Workflow
1. Read data from CSV files.
2. Validate customer and product records.
3. Check orders and payment references.
4. Calculate order amounts using price and quantity.
5. Connect Java to MySQL using JDBC.
6. Store data in MySQL tables.
7. Analyze the data using SQL queries.

## Key Features
- CSV file processing
- Data validation
- Order amount calculation
- MySQL database integration
- Duplicate record handling
- SQL-based data analysis

## SQL Analysis
- Display orders with customer and product details.
- Calculate total sales excluding cancelled orders.
- Identify best-selling products.
- Count customers by city.
- Analyze payment statuses.

## How to Run
1. Install Java and MySQL.
2. Create the `ecommerce` database and required tables.
3. Add MySQL Connector/J to the project.
4. Configure your MySQL credentials in `Main.java`.
5. Run `Main.java` from VS Code.

## Learning Outcomes
- Java programming
- JDBC connectivity
- Relational database management
- SQL queries and joins
- Data validation and ETL concepts

## Future Improvements
- Improve data-quality checks.
- Generate automated reports.
- Add more business analysis queries.
- Improve error handling.