package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(300)
public class ExistingProductIdGenerationStrategy implements ProductIdGenerationStrategy {

    @Override
    public boolean supports(Product product) {
        return product.getProductId() != null && !product.getProductId().isBlank();
    }

    @Override
    public String generate(Product product) {
        return product.getProductId();
    }
}
