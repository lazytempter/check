package main.java.ru.clevertec.check;

public class DiscountCard {

    public int id;
    public Integer number;
    public int discount_amount;

    public DiscountCard(int id, int number, int discount_amount) {
        this.id = id;
        this.number = number;
        this.discount_amount = discount_amount;
    }

    @Override
    public String toString() {
        return "DiscountCard{" +
                "id=" + id +
                ", number=" + number +
                ", discount_amount=" + discount_amount +
                '}';
    }
}
