package main.java.ru.clevertec.check;

public class ShoppingCartProduct {
    public int quantity;
    public String description;
    public double price;
    public double discount;
    public double total;

    public ShoppingCartProduct(int quantity, String description, double price, double discount, double total) {
        this.quantity = quantity;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.total = total;
    }

    @Override
    public String toString() {
        return "ShoppingCartProduct{" +
                "quantity=" + quantity +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", total=" + total +
                '}';
    }
}
