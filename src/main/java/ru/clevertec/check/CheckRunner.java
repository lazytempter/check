package main.java.ru.clevertec.check;

import java.util.Arrays;

public class CheckRunner {
    public static void main(String[] args) {

        if (Arrays.toString(args).contains("pathToFile")) {
            ShoppingCartBuilder shoppingCartBuilder = new ShoppingCartByFileCSVBuilder();
            shoppingCartBuilder.buildShoppingCart(args);
        } else {
            ShoppingCartBuilder shoppingCartBuilder = new ShoppingCartByDefaultCSVBuilder();
            shoppingCartBuilder.buildShoppingCart(args);
        }
    }
}
