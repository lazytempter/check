package main.java.ru.clevertec.check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartByFileCSVBuilder implements ShoppingCartBuilder{

    @Override
    public void buildShoppingCart(String[] inputData) {
        List<String> testDataList = Arrays.asList(inputData);

        Integer discountCardNumber = 0;
        double balanceDebitCard = 0;
        Map<Integer, Integer> customerProducts = new HashMap<>();
        String pathToFile = "";
        String saveToFile = "";

        for (String unit : testDataList) {
            if (unit.contains("-")) {
                int separatorIndex = unit.lastIndexOf('-');
                Integer id = Integer.parseInt(unit.substring(0,separatorIndex));
                Integer amount = Integer.parseInt(unit.substring(separatorIndex + 1));
                if (customerProducts.containsKey(id)) {
                    customerProducts.put(id, customerProducts.get(id) + amount);
                } else {
                    customerProducts.put(id,amount);
                }
            }
            if (unit.contains("discountCard=")) {
                int separatorIndex = unit.lastIndexOf('=');
                discountCardNumber = Integer.parseInt(unit.substring(separatorIndex + 1));
            }
            if (unit.contains("balanceDebitCard=")) {
                int separatorIndex = unit.lastIndexOf('=');
                balanceDebitCard = Double.parseDouble(unit.substring(separatorIndex + 1));
            }
            if (unit.contains("pathToFile=")) {
                int separatorIndex = unit.lastIndexOf('=');
                pathToFile = unit.substring(separatorIndex + 1);
            }
            if (unit.contains("saveToFile=")) {
                int separatorIndex = unit.lastIndexOf('=');
                saveToFile = unit.substring(separatorIndex + 1);
            }
        }

        if (discountCardNumber > 0 && balanceDebitCard > 0 && !customerProducts.isEmpty()) {
            if ((pathToFile.isEmpty() && saveToFile.isEmpty()) || saveToFile.isEmpty()) {
                CashBox.getBadRequestError(null);
            } else if (pathToFile.isEmpty() && !saveToFile.isEmpty()) {
                CashBox.getBadRequestError(saveToFile);
            } else {
                CashBox.buildCheck(discountCardNumber, balanceDebitCard, customerProducts, pathToFile, saveToFile);
            }
        } else {
            CashBox.getBadRequestError(null);
        }
    }
}
