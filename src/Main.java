import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BillingSystem billingSystem = new BillingSystem();
        billingSystem.loadProducts(); // Load products from file

        int choice;

        do {
            System.out.println("\n===== Smart Billing System =====");
            System.out.println("1. View Products");
            System.out.println("2. Search Product");
            System.out.println("3. Add to Cart");
            System.out.println("4. Generate Invoice");
            System.out.println("5. View Daily Sales Summary");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    billingSystem.displayProducts();
                    break;
                case 2:
                    System.out.print("Enter product name or ID to search: ");
                    String search = scanner.nextLine();
                    billingSystem.searchProduct(search);
                    break;
                case 3:
                    billingSystem.addToCart(scanner);
                    break;
                case 4:
                    billingSystem.generateInvoice();
                    break;
                case 5:
                    billingSystem.viewSalesSummary();
                    break;
                case 6:
                    System.out.println("Exiting... Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 6);

        scanner.close();
    }
}
