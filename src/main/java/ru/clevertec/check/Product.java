package main.java.ru.clevertec.check;

import java.util.Map;

public class Product {

    public Integer id;
    public String description;
    public double price;
    public Integer quantity_in_stock;
    public boolean wholesale_product;

    public Product(int id, String description, double price, int quantity_in_stock, boolean wholesale_product) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.quantity_in_stock = quantity_in_stock;
        this.wholesale_product = wholesale_product;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity_in_stock=" + quantity_in_stock +
                ", wholesale_product=" + wholesale_product +
                '}';
    }
}
