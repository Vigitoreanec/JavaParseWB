package org.top.parseozonproduct;

public class Product {
    private String id;
    private String name; //название
    private double price; //цена
    private String brand; //бренд
    private double rating; //рейтинг
    private boolean available; //наличие
    private int feedbackCount; //продажи

    public Product(String id,
                   String name,
                   double price,
                   String brand,
                   double rating,
                   boolean available,
                   int feedbackCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.rating = rating;
        this.available = available;
        this.feedbackCount = feedbackCount;
    }

    @Override
    public String toString() {
        return String.format(
                "Product{id='%s', название='%s', " +
                        "цена=%.2f руб., бренд='%s', " +
                        "рейтинг=%.1f, наличие=%s, продажи=%d}",
                id, name, price, brand, rating,
                available, feedbackCount
        );
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getBrand() { return brand; }
    public double getRating() { return rating; }
    public boolean isAvailable() { return available; }
    public int getFeedbackCount() { return feedbackCount; }
}
