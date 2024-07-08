package main.java.ru.clevertec.check;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CashBox {

    final static String PATH_TO_PRODUCTS = "./src/main/resources/products.csv";
    final static String PATH_TO_DISCOUNT_CARDS = "./src/main/resources/discountCards.csv";

    public static void buildCheck (Integer discountCardNumber, double balanceDebitCard, Map<Integer, Integer> customerProducts, String pathToFile, String saveToFile) {

        boolean isDiscountCard = false;

        Map<Integer,Product> availableProducts = getAvailableProducts(pathToFile);
        Map<Integer,DiscountCard> availableDiscountCards = getAvailableDiscountCards();

        if (discountCardNumber != null && availableDiscountCards.containsKey(discountCardNumber)) {
            isDiscountCard = true;
        }

        List<ShoppingCartProduct> shoppingCart = addProductsToShoppingCart(availableProducts, customerProducts);
        if (!shoppingCart.isEmpty()) {
            if (!isDiscountCard) {
                getResult(shoppingCart, null, balanceDebitCard, saveToFile);
            } else {
                useCustomerDiscountCard(shoppingCart, availableDiscountCards.get(discountCardNumber));
                getResult(shoppingCart, availableDiscountCards.get(discountCardNumber), balanceDebitCard, saveToFile);
            }
        }
    }

    public static Map<Integer,Product> getAvailableProducts (String pathToFile) {

        Map<Integer,Product> availableProducts = new HashMap<>();
        List<List<String>> products;
        products = readData(Objects.requireNonNullElse(pathToFile, PATH_TO_PRODUCTS));
        for (List<String> row : products) {

            Product product = new Product(
                    Integer.parseInt(row.get(0)),
                    row.get(1),
                    Double.parseDouble(row.get(2)),
                    Integer.parseInt(row.get(3)),
                    Boolean.parseBoolean(row.get(4))
            );

            availableProducts.put(product.id, product);
        }
        return availableProducts;
    }

    public static Map<Integer,DiscountCard> getAvailableDiscountCards () {

        Map<Integer,DiscountCard> availableDiscountCards = new HashMap<>();

        List<List<String>> discountCards = readData(PATH_TO_DISCOUNT_CARDS);
        for (List<String> row : discountCards) {

            DiscountCard discountCard = new DiscountCard(
                    Integer.parseInt(row.get(0)),
                    Integer.parseInt(row.get(1)),
                    Integer.parseInt(row.get(2))
            );

            availableDiscountCards.put(discountCard.number, discountCard);
        }
        return availableDiscountCards;
    }

    public static List<ShoppingCartProduct> addProductsToShoppingCart(Map<Integer,Product> availableProducts, Map<Integer, Integer> customerProducts) {

        List<ShoppingCartProduct> shoppingCart = new ArrayList<>();
        for (Integer id : customerProducts.keySet()) {
            if (availableProducts.containsKey(id) &&
                availableProducts.get(id).quantity_in_stock >= customerProducts.get(id)) {

                ShoppingCartProduct shoppingCartProduct = new ShoppingCartProduct(
                        customerProducts.get(id),
                        availableProducts.get(id).description,
                        availableProducts.get(id).price,
                        0,
                        availableProducts.get(id).price * customerProducts.get(id)
                );

                if (availableProducts.get(id).wholesale_product && customerProducts.get(id) >= 5) {
                    shoppingCartProduct.discount = (double) Math.round((shoppingCartProduct.total * 0.1) * 100) / 100;
                }

                shoppingCart.add(shoppingCartProduct);
            } else {
                getBadRequestError(null);
                shoppingCart.clear();
                break;
            }
        }
        return shoppingCart;
    }

    public static void useCustomerDiscountCard (List<ShoppingCartProduct> shoppingCart, DiscountCard discountCard) {

        for (ShoppingCartProduct product : shoppingCart) {
            if (product.discount == 0) {
                product.discount = (double) Math.round((product.total * ((double) discountCard.discount_amount / 100)) * 100) / 100;
            }
        }
    }

    public static void getResult(List<ShoppingCartProduct> shoppingCart, DiscountCard discountCard, double balanceDebitCard, String saveToFile) {

        double total = 0;
        double totalDiscount = 0;
        double totalWithDiscount = 0;
        for (ShoppingCartProduct scp : shoppingCart) {
            total += scp.total;
            totalDiscount += scp.discount;
        }
        total = (double) Math.round(total * 100) / 100;
        totalDiscount = (double) Math.round(totalDiscount * 100) / 100;
        totalWithDiscount = total - totalDiscount;
        if (balanceDebitCard - totalWithDiscount < 0) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Objects.requireNonNullElse(saveToFile, "error_result.csv")))) {
                System.out.println(" ");
                System.out.println("ERROR");
                System.out.println("NOT ENOUGH MONEY");

                bufferedWriter.write("ERROR");
                bufferedWriter.newLine();
                bufferedWriter.write("NOT ENOUGH MONEY");
                bufferedWriter.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Objects.requireNonNullElse(saveToFile, "result.csv")))) {

                String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

                bufferedWriter.write("Date;Time");
                bufferedWriter.newLine();
                bufferedWriter.write(date + ";" + time);
                bufferedWriter.newLine();
                bufferedWriter.newLine();

                System.out.println("Date;Time");
                System.out.println(date + ";" + time);
                System.out.println(" ");

                bufferedWriter.write("QTY;DESCRIPTION;PRICE;DISCOUNT;TOTAL");
                bufferedWriter.newLine();

                System.out.println("QTY;DESCRIPTION;PRICE;DISCOUNT;TOTAL");

                for (ShoppingCartProduct scp : shoppingCart) {
                    bufferedWriter.write(scp.quantity + ";"
                            + scp.description +";"
                            + scp.price + "$;"
                            + scp.discount + "$;"
                            + scp.total + "$");
                    bufferedWriter.newLine();

                    System.out.println(scp.quantity + ";"
                            + scp.description +";"
                            + scp.price + "$;"
                            + scp.discount + "$;"
                            + scp.total + "$");
                }

                if (discountCard != null) {
                    bufferedWriter.newLine();
                    bufferedWriter.write("DISCOUNT CARD;DISCOUNT PERCENTAGE");
                    bufferedWriter.newLine();
                    bufferedWriter.write(discountCard.number + ";" + discountCard.discount_amount + "%");
                    bufferedWriter.newLine();

                    System.out.println(" ");
                    System.out.println("DISCOUNT CARD;DISCOUNT PERCENTAGE");
                    System.out.println(discountCard.number + ";" + discountCard.discount_amount + "%");
                }

                bufferedWriter.newLine();
                bufferedWriter.write("TOTAL PRICE;TOTAL DISCOUNT;TOTAL WITH DISCOUNT");
                bufferedWriter.newLine();
                bufferedWriter.write(total + "$;" + totalDiscount + "$;" + totalWithDiscount + "$");

                System.out.println(" ");
                System.out.println("TOTAL PRICE;TOTAL DISCOUNT;TOTAL WITH DISCOUNT");
                System.out.println(total + "$;" + totalDiscount + "$;" + totalWithDiscount + "$");

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static List<List<String>> readData(String path) {

        List<List<String>> data = new ArrayList<>();
        String line;
        String delimiter = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null) {
                if (!line.contains("id")) {
                    String editedLine = line.replace("\"", "");
                    String[] values = editedLine.split(delimiter);
                    data.add(Arrays.asList(values));
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    public static void getBadRequestError (String path) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Objects.requireNonNullElse(path, "error_result.csv"))))  {
            bufferedWriter.write("ERROR");
            bufferedWriter.newLine();
            bufferedWriter.write("BAD REQUEST");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
