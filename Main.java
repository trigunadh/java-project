import java.util.*;
import java.io.*;
class Product implements Serializable {
    String id;
    String name;
    String category;
    double price;

    Product(String id, String name, String category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String toString() {
        return id + " | " + name + " | " + category + " | Rs." + price;
    }
}


class CartItem implements Serializable {
    String productId;
    int quantity;

    CartItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}


class CustomerCart implements Serializable {
    String customerId;
    ArrayList<CartItem> items = new ArrayList<>();

    CustomerCart(String customerId) {
        this.customerId = customerId;
    }
}


@FunctionalInterface
interface ProductFilter {
    boolean test(Product p);
}


class Order {
    static int orderNumber = 1001;

    void placeOrder(CustomerCart cart, HashMap<String, Product> products) {

        if (cart.items.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }

        double total = 0;

        System.out.println("\n========== ORDER RECEIPT ==========");

        System.out.println("Order ID: " + orderNumber++);
        System.out.println("Customer ID: " + cart.customerId);

        for (CartItem item : cart.items) {
            Product p = products.get(item.productId);

            if (p != null) {
                double amount = p.price * item.quantity;

                System.out.println(p.name + " x " + item.quantity
                        + " = Rs." + amount);

                total += amount;
            }
        }

        System.out.println("-----------------------------------");
        System.out.println("Total Amount: Rs." + total);
        System.out.println("Order placed successfully!");
        System.out.println("===================================");

        cart.items.clear();
    }
}

// ShoppingService class
class ShoppingService {

    HashMap<String, Product> products = new HashMap<>();
    CustomerCart cart;

    ShoppingService(String customerId) {

        cart = new CustomerCart(customerId);

        products.put("P101",
                new Product("P101", "Mouse", "Electronics", 599));

        products.put("P102",
                new Product("P102", "Keyboard", "Electronics", 899));

        products.put("P103",
                new Product("P103", "Bottle", "Home", 299));

        products.put("P104",
                new Product("P104", "Notebook", "Stationery", 99));

        products.put("P105",
                new Product("P105", "Headphones", "Electronics", 1499));

        products.put("P106",
                new Product("P106", "Desk Lamp", "Home", 799));
    }

    // Display all products
    void displayProducts() {

        System.out.println("\nAvailable Products:");

        for (Product p : products.values()) {
            System.out.println(p);
        }
    }

    // Filter products using Functional Interface
    void filterProducts(ProductFilter filter) {

        boolean found = false;

        for (Product p : products.values()) {

            if (filter.test(p)) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching products found.");
        }
    }

    // Add product to cart
    void addToCart(String id, int quantity) {

        if (!products.containsKey(id) || quantity <= 0) {
            System.out.println("Invalid product ID or quantity.");
            return;
        }

        for (CartItem item : cart.items) {

            if (item.productId.equals(id)) {
                item.quantity += quantity;
                System.out.println("Quantity updated!");
                return;
            }
        }

        cart.items.add(new CartItem(id, quantity));

        System.out.println("Product added to cart!");
    }

    // Display cart
    void displayCart() {

        if (cart.items.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }

        double total = 0;

        System.out.println("\nYour Shopping Cart:");

        for (CartItem item : cart.items) {

            Product p = products.get(item.productId);

            if (p != null) {

                double amount = p.price * item.quantity;

                System.out.println(p.name + " x " + item.quantity
                        + " = Rs." + amount);

                total += amount;
            }
        }

        System.out.println("Total: Rs." + total);
    }

    // Remove product from cart
    void removeFromCart(String id) {

        boolean removed = cart.items.removeIf(
                item -> item.productId.equals(id)
        );

        if (removed) {
            System.out.println("Product removed!");
        } else {
            System.out.println("Product not found in cart.");
        }
    }

    // Serialization
    void saveCart() {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream("cart.ser"))) {

            out.writeObject(cart);

            System.out.println("Cart saved successfully!");

        } catch (IOException e) {

            System.out.println("Error saving cart: " + e.getMessage());
        }
    }

    // Deserialization
    void loadCart() {

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream("cart.ser"))) {

            cart = (CustomerCart) in.readObject();

            System.out.println("Cart restored successfully!");

        } catch (IOException | ClassNotFoundException e) {

            System.out.println("Error loading cart: " + e.getMessage());
        }
    }

    // Checkout
    void checkout() {

        Order order = new Order();

        order.placeOrder(cart, products);
    }
}

// Main class
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Customer ID: ");
        String customerId = sc.nextLine();

        ShoppingService shop = new ShoppingService(customerId);

        int choice;

        do {

            System.out.println("\n===== ONLINE SHOPPING SYSTEM =====");

            System.out.println("1. Display Products");
            System.out.println("2. Filter Products by Price");
            System.out.println("3. Filter Products by Category");
            System.out.println("4. Add Product to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Remove Product from Cart");
            System.out.println("7. Save Cart");
            System.out.println("8. Restore Cart");
            System.out.println("9. Checkout");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:

                    shop.displayProducts();
                    break;

                case 2:

                    System.out.print("Enter maximum price: ");
                    double maxPrice = sc.nextDouble();

                    shop.filterProducts(
                            p -> p.price <= maxPrice
                    );

                    break;

                case 3:

                    System.out.print("Enter category: ");
                    String category = sc.nextLine();

                    shop.filterProducts(
                            p -> p.category.equalsIgnoreCase(category)
                    );

                    break;

                case 4:

                    shop.displayProducts();

                    System.out.print("Enter Product ID: ");
                    String id = sc.nextLine();

                    System.out.print("Enter Quantity: ");
                    int quantity = sc.nextInt();

                    shop.addToCart(id, quantity);

                    break;

                case 5:

                    shop.displayCart();
                    break;

                case 6:

                    System.out.print("Enter Product ID: ");
                    String removeId = sc.nextLine();

                    shop.removeFromCart(removeId);

                    break;

                case 7:

                    shop.saveCart();
                    break;

                case 8:

                    shop.loadCart();
                    break;

                case 9:

                    shop.checkout();
                    break;

                case 0:

                    System.out.println("Thank you for shopping!");
                    break;

                default:

                    System.out.println("Invalid choice!");
            }

        } while (choice != 0);

        sc.close();
    }
}