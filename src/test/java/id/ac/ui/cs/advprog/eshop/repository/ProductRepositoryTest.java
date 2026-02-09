package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
class ProductRepositoryTest {

    ProductRepository productRepository;
    Product product;

    @BeforeEach
    void setUp() {
        this.productRepository = new ProductRepository();
        this.product = new Product();
        this.product.setProductId("product-1");
        this.product.setProductName("Old Name");
        this.product.setProductQuantity(10);
        this.productRepository.create(this.product);
    }

    @Test
    void testUpdateExistingProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setProductId("product-1");
        updatedProduct.setProductName("New Name");
        updatedProduct.setProductQuantity(20);

        Product result = this.productRepository.update(updatedProduct);

        assertNotNull(result);
        assertEquals("product-1", result.getProductId());
        assertEquals("New Name", result.getProductName());
        assertEquals(20, result.getProductQuantity());
    }

    @Test
    void testUpdateMissingProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setProductId("missing-product");
        updatedProduct.setProductName("New Name");
        updatedProduct.setProductQuantity(20);

        Product result = this.productRepository.update(updatedProduct);

        assertNull(result);
    }

    @Test
    void testDeleteExistingProduct() {
        Product result = this.productRepository.delete("product-1");

        assertNotNull(result);
        assertEquals("product-1", result.getProductId());
        Iterator<Product> productIterator = this.productRepository.findAll();
        assertFalse(productIterator.hasNext());
    }

    @Test
    void testDeleteMissingProduct() {
        Product result = this.productRepository.delete("missing-product");

        assertNull(result);
    }
}
