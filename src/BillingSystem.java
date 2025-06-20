import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BillingSystem {
    private List<Product> productList = new ArrayList<>();
    private List<CartItem> cart = new ArrayList<>();
    private final String PRODUCT_FILE = "src\\products.txt";

    // Load product list from file
    public void loadProducts() {
        productList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String id = parts[0];
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    int stock = Integer.parseInt(parts[3]);
                    productList.add(new Product(id, name, price, stock));
                }
            }
            System.out.println("Products loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    // Display all products
    public void displayProducts() {
        System.out.println("\nAvailable Products:");
        System.out.println("----------------------------");
        for (Product p : productList) {
            System.out.println(p.toString());
        }
    }

    public void searchProduct(String search) {
        boolean found = false;
        System.out.println("\nSearch Results for: " + search);
        System.out.println("----------------------------");

        for (Product p : productList) {
            if (p.getName().toLowerCase().contains(search.toLowerCase()) ||
                    p.getId().equalsIgnoreCase(search)) {
                System.out.println(p.toString());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching product found.");
        }
    }

    public void addToCart(Scanner scanner) {
        System.out.print("\nEnter Product ID to add to cart: ");
        String productId = scanner.nextLine();
        Product selectedProduct = null;

        // Find product by ID
        for (Product p : productList) {
            if (p.getId().equalsIgnoreCase(productId)) {
                selectedProduct = p;
                break;
            }
        }

        if (selectedProduct == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // consume leftover newline

        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
        } else if (quantity > selectedProduct.getStock()) {
            System.out.println("Not enough stock available.");
        } else {
            cart.add(new CartItem(selectedProduct, quantity));
            selectedProduct.setStock(selectedProduct.getStock() - quantity); // reduce stock
            System.out.println("Added to cart.");
        }
    }

    public void generateInvoice() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Add items first.");
            return;
        }

        double total = 0;
        System.out.println("\nInvoice:");
        System.out.println("----------------------------");
        for (CartItem item : cart) {
            System.out.println(item);
            total += item.getTotalPrice();
        }

        // Apply discount
        double discount = 0;
        if (total >= 5000) {
            discount = total * 0.10; // 10% discount
        }

        double finalAmount = total - discount;

        System.out.println("----------------------------");
        System.out.println("Total: ₹" + total);
        System.out.println("Discount: ₹" + discount);
        System.out.println("Payable Amount: ₹" + finalAmount);

        // Save transaction to file
        saveTransaction(total, discount, finalAmount);

        // Clear cart
        cart.clear();
        System.out.println("Invoice generated and saved successfully.");
    }

    private void saveTransaction(double total, double discount, double finalAmount) {
        String fileName = "transactions.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String time = LocalDateTime.now().format(formatter);

            writer.write("Date: " + time);
            writer.newLine();
            for (CartItem item : cart) {
                writer.write(item.toString());
                writer.newLine();
            }
            writer.write("Total: ₹" + total);
            writer.newLine();
            writer.write("Discount: ₹" + discount);
            writer.newLine();
            writer.write("Final Amount: ₹" + finalAmount);
            writer.newLine();
            writer.write("--------------------------------------------------");
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    public void viewSalesSummary() {
        String fileName = "transactions.txt";
        int totalTransactions = 0;
        double totalSales = 0.0;

        LocalDateTime today = LocalDateTime.now();
        String todayDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isToday = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Date:")) {
                    isToday = line.contains(todayDate);
                    if (isToday) totalTransactions++;
                }

                if (isToday && line.startsWith("Final Amount: ₹")) {
                    String[] parts = line.split("₹");
                    if (parts.length == 2) {
                        totalSales += Double.parseDouble(parts[1]);
                    }
                }
            }

            System.out.println("\nDaily Sales Summary (" + todayDate + ")");
            System.out.println("----------------------------------");
            System.out.println("Total Transactions: " + totalTransactions);
            System.out.println("Total Sales Amount: ₹" + totalSales);

        } catch (IOException e) {
            System.out.println("Error reading sales summary: " + e.getMessage());
        }
    }

}
