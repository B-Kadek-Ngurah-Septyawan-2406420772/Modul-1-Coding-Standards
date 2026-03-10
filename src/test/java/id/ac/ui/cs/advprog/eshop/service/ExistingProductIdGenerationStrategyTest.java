package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExistingProductIdGenerationStrategyTest {

    private final ExistingProductIdGenerationStrategy strategy = new ExistingProductIdGenerationStrategy();

    @Test
    void testSupportsReturnsTrueForNonBlankProductId() {
        Product product = new Product();
        product.setProductId("product-1");

        assertTrue(strategy.supports(product));
        assertEquals("product-1", strategy.generate(product));
    }

    @Test
    void testSupportsReturnsFalseForBlankProductId() {
        Product product = new Product();
        product.setProductId(" ");

        assertFalse(strategy.supports(product));
    }

    @Test
    void testSupportsReturnsFalseForNullProductId() {
        Product product = new Product();

        assertFalse(strategy.supports(product));
    }
}
