package dss.example.pr1.service;

import dss.example.pr1.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
@SessionScope
public class CartService {

    private final List<Product> items = new ArrayList<>();

    public void addItem(Product product) {
        items.add(product);
    }

    public void removeItem(Long id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(id)) {
                items.remove(i);
                break;
            }
        }
    }

    public List<Product> removeIf(Predicate<Product> condition) {
        List<Product> removed = new ArrayList<>();

        for (Product product : items) {
            if (condition.test(product)) {
                removed.add(product);
            }
        }

        items.removeIf(condition);
        return removed;
    }

    public List<Product> getItems() {
        return items;
    }

    public double getTotal() {
        double total = 0;

        for (Product product : items) {
            total += product.getPrice();
        }

        return total;
    }
}