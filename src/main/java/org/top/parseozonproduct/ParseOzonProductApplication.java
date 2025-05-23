package org.top.parseozonproduct;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class ParseOzonProductApplication {

    public static void main(String[] args) throws IOException {
        URL url = new URL("https://search.wb.ru/exactmatch/ru/common/v13/search?ab_testing=false&appType=1&curr=rub&dest=-1185367&hide_dtype=13&lang=ru&page=1&query=%D0%BD%D0%B0%D0%B4%D1%83%D0%B2%D0%BD%D0%B0%D1%8F%20%D0%BF%D0%B0%D0%BB%D0%B0%D1%82%D0%BA%D0%B0%20%D0%B4%D0%BE%D0%BC%D0%B8%D0%BA&resultset=catalog&sort=popular&spp=30&suppressSpellcheck=false&xsubject=498;1267");
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        httpConn.setRequestProperty("accept", "*/*");
        httpConn.setRequestProperty("accept-language", "ru,ru-RU;q=0.9,en-US;q=0.8,en;q=0.7");
        httpConn.setRequestProperty("origin", "https://www.wildberries.ru");
        httpConn.setRequestProperty("priority", "u=1, i");
        httpConn.setRequestProperty("referer", "https://www.wildberries.ru/catalog/0/search.aspx?page=1&sort=popular&search=%D0%BD%D0%B0%D0%B4%D1%83%D0%B2%D0%BD%D0%B0%D1%8F+%D0%BF%D0%B0%D0%BB%D0%B0%D1%82%D0%BA%D0%B0+%D0%B4%D0%BE%D0%BC%D0%B8%D0%BA&xsubject=498%3B1267&targeturl=ST");
        httpConn.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"");
        httpConn.setRequestProperty("sec-ch-ua-mobile", "?0");
        httpConn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
        httpConn.setRequestProperty("sec-fetch-dest", "empty");
        httpConn.setRequestProperty("sec-fetch-mode", "cors");
        httpConn.setRequestProperty("sec-fetch-site", "cross-site");
        httpConn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36");
        httpConn.setRequestProperty("x-captcha-id", "Catalog 1|1|1748049292|AA==|d4f16deabbb048ab848b7f180f53b651|rev0Dyo2xqU0uc183VQd8nlwaa2bwxSD2qEOtnx4LUW");
        httpConn.setRequestProperty("x-queryid", "qid736597083174803489220250523211454");
        httpConn.setRequestProperty("x-userid", "0");

        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String response = s.hasNext() ? s.next() : "";
        // Проверка правильного кода ответа
        int statusCode = httpConn.getResponseCode();
        System.out.println("HTTP Status: " + statusCode);
        //System.out.println(response);
        if(statusCode == 200) {
            List<Product> products = parseProductsFromJson(response);
            products.forEach(System.out::println);
            System.out.println("Found products: " + products.size());
            // Пример реализации и сортировки
            analyzeProducts(products);
        }
    }
    private static List<Product> parseProductsFromJson(String jsonResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonResponse);
            JsonNode productsNode = rootNode.path("data").path("products");

            List<Product> products = new ArrayList<>();
            for (JsonNode productNode : productsNode) {
                Product product = new Product(
                        productNode.path("id").asText(),
                        productNode.path("name").asText(),
                        productNode.path("sizes").findPath("price").findPath("total").asDouble() / 100, // Цена в рублях (делим на 100)
                        productNode.path("brand").asText(),
                        productNode.path("reviewRating").asDouble(),
                        productNode.path("sale").asInt() > 0, // Доступен, если есть продажи
                        productNode.path("feedbacks").asInt()
                );
                products.add(product);
            }
            return products;
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    private static void analyzeProducts(List<Product> products) {
        System.out.println("\n=== Анализ данных ===");
        System.out.println("\nТоп-20 товаров по цене:");
        products.stream()
                .sorted((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()))
                .limit(20)
                .forEach(p -> System.out.printf("%n \t%s (%.1f) - %.2f руб. - Колличество заказов: %d",
                        p.getName(), p.getRating(), p.getPrice(), p.getFeedbackCount()));
    }
}
