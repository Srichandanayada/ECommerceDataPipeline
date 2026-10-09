import java.util.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Main {
    static Set<String> productIds = new HashSet<>();
     static Set<String> orderIds = new HashSet<>();
     static Set<String> customerIds = new HashSet<>();
     static  Set<String> paymentIds = new HashSet<>();

     static Map<String, Double> productPrices = new HashMap<>();

     //jdbc connection
     static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/ecommerce",
        "root",
        "YOUR PASSWORD"
    );
}
static void insertCustomers() {

    String sql = "INSERT IGNORE INTO customers " +
                 "(customerId, name, email, city, state, signup_date) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";

    try {
        Connection con = getConnection();

        PreparedStatement ps = con.prepareStatement(sql);

        BufferedReader br = new BufferedReader(
            new FileReader("data/customers.csv")
        );

        String line;
        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            ps.setString(1, data[0]);
            ps.setString(2, data[1]);
            ps.setString(3, data[2]);
            ps.setString(4, data[3]);
            ps.setString(5, data[4]);
            ps.setDate(6, java.sql.Date.valueOf(data[5]));

            ps.executeUpdate();

            System.out.println( "Inserted customer: " + data[0] );
        }

        br.close();
        ps.close();
        con.close();

    } catch (Exception e) {
        System.out.println("Error inserting customers");
        e.printStackTrace();
    }
}

static void insertProducts() {

    String sql = "INSERT IGNORE INTO products " +
                 "(productId, productName, category, price) " +
                 "VALUES (?, ?, ?, ?)";

    try {
        Connection con = getConnection();

        PreparedStatement ps = con.prepareStatement(sql);

        BufferedReader br = new BufferedReader(
            new FileReader("data/products.csv")
        );

        String line;
        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            String productId = data[0];
            String productName = data[1];
            String category = data[2];
            double price = Double.parseDouble(data[3]);

            ps.setString(1, productId);
            ps.setString(2, productName);
            ps.setString(3, category);
            ps.setDouble(4, price);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Inserted product: " + productId);
            } else {
                System.out.println("Product already exists: " + productId);
            }
        }

        br.close();
        ps.close();
        con.close();

    } catch (Exception e) {
        System.out.println("Error inserting products");
        e.printStackTrace();
    }
}

static void insertOrders() {

    String sql = "INSERT IGNORE INTO orders " +
                 "(orderId, customerId, productId, quantity, " +
                 "orderDate, status, orderAmount) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try {
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        BufferedReader br = new BufferedReader(
            new FileReader("data/orders.csv")
        );

        String line;
        br.readLine();

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            String orderId = data[0];
            String customerId = data[1];
            String productId = data[2];
            int quantity = Integer.parseInt(data[3]);
            String orderDate = data[4];
            String status = data[5];

            if (!exists(con, "customers", "customerId", customerId)) {
                System.out.println("Customer not found: " + customerId);
                continue;
            }

            if (!exists(con, "products", "productId", productId)) {
                System.out.println("Product not found: " + productId);
                continue;
            }

            if (quantity <= 0) {
                System.out.println("Invalid quantity: " + orderId);
                continue;
            }

            double price;

            String priceSql =
                "SELECT price FROM products WHERE productId = ?";

            try (PreparedStatement pricePs =
                     con.prepareStatement(priceSql)) {

                pricePs.setString(1, productId);

                try (ResultSet rs = pricePs.executeQuery()) {
                    if (!rs.next()) {
                        continue;
                    }

                    price = rs.getDouble("price");
                }
            }

            double orderAmount = price * quantity;

            ps.setString(1, orderId);
            ps.setString(2, customerId);
            ps.setString(3, productId);
            ps.setInt(4, quantity);
            ps.setDate(5, java.sql.Date.valueOf(orderDate));
            ps.setString(6, status);
            ps.setDouble(7, orderAmount);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Inserted order: " + orderId);
            } else {
                System.out.println("Order already exists: " + orderId);
            }
        }

        br.close();
        ps.close();
        con.close();

    } catch (Exception e) {
        System.out.println("Error inserting orders");
        e.printStackTrace();
    }
}

static void insertPayments() {

    String sql = "INSERT IGNORE INTO payments " +
                 "(paymentId, orderId, paymentMethod, " +
                 "paymentStatus, paymentDate) " +
                 "VALUES (?, ?, ?, ?, ?)";

    try {
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        BufferedReader br = new BufferedReader(
            new FileReader("data/payments.csv")
        );

        String line;
        br.readLine();

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            String paymentId = data[0];
            String orderId = data[1];
            String paymentMethod = data[2];
            String paymentStatus = data[3];
            String paymentDate = data[4];

            if (!exists(con, "orders", "orderId", orderId)) {
                System.out.println("Order not found: " + orderId);
                continue;
            }

            if (paymentMethod.isEmpty() ||
                paymentStatus.isEmpty() ||
                paymentDate.isEmpty()) {

                System.out.println("Invalid payment: " + paymentId);
                continue;
            }

            ps.setString(1, paymentId);
            ps.setString(2, orderId);
            ps.setString(3, paymentMethod);
            ps.setString(4, paymentStatus);
            ps.setDate(5, java.sql.Date.valueOf(paymentDate));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Inserted payment: " + paymentId);
            } else {
                System.out.println("Payment already exists: " + paymentId);
            }
        }

        br.close();
        ps.close();
        con.close();

    } catch (Exception e) {
        System.out.println("Error inserting payments");
        e.printStackTrace();
    }
}

static boolean exists(
        Connection con, String table, String column, String value)
        throws SQLException {

    String sql = "SELECT 1 FROM " + table +
                 " WHERE " + column + " = ? LIMIT 1";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, value);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}

    public static void main(String[] args) {

        try {
            // Test database connection
            Connection con = getConnection();
            System.out.println("Database connected successfully!");
            con.close();

        readCustomerData();
        insertCustomers();
        readProducts();
        insertProducts();
        readOrders();
        insertOrders();
        readPayments();
        insertPayments();

         }
         
         catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
        
    }

    static void readCustomerData() {
        try {
            BufferedReader br = new BufferedReader(
                new FileReader("data/customers.csv")
            );

            String line=br.readLine(); // Read the header line
            
            /*while ((line = br.readLine()) != null) {
                System.out.println(line);
            }*/
           while ((line = br.readLine()) != null) {

                    String[] data = line.split(",");

                    String customerId = data[0];
                    String name = data[1];
                    String email = data[2];
                    String signupDate = data[5];

                    // Checking duplicate ID
                if (customerIds.contains(customerId)) {
                    System.out.println("Duplicate customer: " + customerId);
                    continue;
                }
                customerIds.add(customerId);

                boolean valid = true;

                    if (customerId.isEmpty()) {
                        System.out.println("Customer ID is missing");
                        valid = false;
                    }

                    if (name.isEmpty()) {
                        System.out.println("Name is missing");
                        valid = false;
                    }

                    if (!email.contains("@")) {
                        System.out.println("Invalid email");
                        valid = false;
                    }

                    if (signupDate.isEmpty()) {
                        System.out.println("Signup date is missing");
                        valid = false;
                    }

                    if (valid) {
                        System.out.println("Valid customer: " + customerId);
                    } else {
                        System.out.println("Invalid customer: " + customerId);
                    }
                }

            br.close();

        } catch (IOException e) {
            System.out.println("Error reading file");
        }
    }
    static void readProducts() {

    try {
        BufferedReader br = new BufferedReader(
            new FileReader("data/products.csv")
        );

        String line = br.readLine(); // skip header

        while ((line = br.readLine()) != null) {

            String[] data = line.split(",");

            String productId = data[0];
            String productName = data[1];
            String category = data[2];
            double price = Double.parseDouble(data[3]);

            boolean valid = true;

            if (productId.isEmpty()) {
                System.out.println("Product ID is missing");
                valid = false;
            }

            if (productName.isEmpty()) {
                System.out.println("Product name is missing");
                valid = false;
            }

            if (category.isEmpty()) {
                System.out.println("Category is missing");
                valid = false;
            }

            if (price <= 0) {
                System.out.println("Invalid price");
                valid = false;
            }

            if (valid) {
                productIds.add(productId);
                productPrices.put(productId, price);

                System.out.println("Valid product: " + productId);
            } else {
                System.out.println("Invalid product: " + productId);
            }
        }

        br.close();

    } catch (IOException e) {
        System.out.println("Error reading products file");
    }
}
static void readOrders() {

        try {
            BufferedReader br = new BufferedReader(
                new FileReader("data/orders.csv")
            );

            String line = br.readLine();

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                String orderId = data[0];
                String customerId = data[1];
                String productId = data[2];
                int quantity = Integer.parseInt(data[3]);
                String orderDate = data[4];
                String status = data[5];

                boolean valid = true;

                if (orderIds.contains(orderId)) {
                    System.out.println("Duplicate order: " + orderId);
                    continue;
                }

                orderIds.add(orderId);

                if (orderId.isEmpty()) {
                    System.out.println("Order ID is missing");
                    valid = false;
                }

                if (customerId.isEmpty()) {
                    System.out.println("Customer ID is missing");
                    valid = false;
                }

                if (productId.isEmpty()) {
                    System.out.println("Product ID is missing");
                    valid = false;
                }

                if (quantity <= 0) {
                    System.out.println("Invalid quantity");
                    valid = false;
                }

                if (orderDate.isEmpty()) {
                    System.out.println("Order date is missing");
                    valid = false;
                }

                if (status.isEmpty()) {
                    System.out.println("Status is missing");
                    valid = false;
                }
                if (!customerIds.contains(customerId)) {
                    System.out.println("Customer does not exist: " + customerId);
                    valid = false;
                }

                if (!productIds.contains(productId)) {
                    System.out.println(
                        "Product does not exist: " + productId
                    );
                    valid = false;
                }

                if (valid) {

                    double price = productPrices.get(productId);

                    double orderAmount = price * quantity;

                    orderIds.add(orderId);

                    System.out.println("Valid order: " + orderId);
                    System.out.println("Order Amount: " + orderAmount);

                } else {
                    System.out.println("Invalid order: " + orderId);
                }
              
            }

            br.close();

        } catch (IOException e) {
            System.out.println("Error reading orders file");
        }
    }
     static void readPayments() {

        try {
            BufferedReader br = new BufferedReader(
                new FileReader("data/payments.csv")
            );

            String line = br.readLine();

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                String paymentId = data[0];
                String orderId = data[1];
                String paymentMethod = data[2];
                String paymentStatus = data[3];
                String paymentDate = data[4];

                boolean valid = true;

                if (paymentIds.contains(paymentId)) {
                    System.out.println("Duplicate payment: " + paymentId);
                    continue;
                }

                paymentIds.add(paymentId);

                if (paymentId.isEmpty()) {
                    System.out.println("Payment ID is missing");
                    valid = false;
                }

                if (orderId.isEmpty()) {
                    System.out.println("Order ID is missing");
                    valid = false;
                }

                if (paymentMethod.isEmpty()) {
                    System.out.println("Payment method is missing");
                    valid = false;
                }

                if (paymentStatus.isEmpty()) {
                    System.out.println("Payment status is missing");
                    valid = false;
                }

                if (paymentDate.isEmpty()) {
                    System.out.println("Payment date is missing");
                    valid = false;
                }

                 if (!orderIds.contains(orderId)) {
                System.out.println("Order does not exist: " + orderId);
                valid = false;
            }

            if (valid) {
                paymentIds.add(paymentId);

                System.out.println("Valid payment: " + paymentId);
            } else {
                System.out.println("Invalid payment: " + paymentId);
            }
            }

            br.close();

        } catch (IOException e) {
            System.out.println("Error reading payments file");
        }
    }
}