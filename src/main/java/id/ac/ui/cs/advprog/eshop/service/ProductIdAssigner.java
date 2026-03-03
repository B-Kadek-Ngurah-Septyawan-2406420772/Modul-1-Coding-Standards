package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ProductIdAssigner {

    private final List<ProductIdGenerationStrategy> strategies;

    public ProductIdAssigner(List<ProductIdGenerationStrategy> strategies) {
        this.strategies = new ArrayList<>(strategies);
    }

    public String assign(Product product) {
        Objects.requireNonNull(product, "product must not be null");
        for (ProductIdGenerationStrategy strategy : strategies) {
            if (strategy.supports(product)) {
                return strategy.generate(product);
            }
        }
        throw new IllegalStateException("No ProductIdGenerationStrategy can handle product id assignment");
    }
}
