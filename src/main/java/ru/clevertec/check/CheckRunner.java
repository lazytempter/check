package main.java.ru.clevertec.check;

public class CheckRunner {
    public static void main(String[] args) {

        ShoppingCartBuilder shoppingCartBuilder = new ShoppingCartByDefaultCSVBuilder();
        shoppingCartBuilder.buildShoppingCart(args);

    }
}
