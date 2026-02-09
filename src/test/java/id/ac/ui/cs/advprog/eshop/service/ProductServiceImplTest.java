package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
class ProductServiceImplTest {

    ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new ProductRepository();
        this.productService = new ProductServiceImpl();
        ReflectionTestUtils.setField(this.productService, "productRepository", productRepository);
    }

    @Test
    void testUpdateExistingProduct() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Old Name");
        product.setProductQuantity(10);
        this.productService.create(product);

        Product updatedProduct = new Product();
        updatedProduct.setProductId("product-1");
        updatedProduct.setProductName("New Name");
        updatedProduct.setProductQuantity(20);

        Product result = this.productService.update(updatedProduct);

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

        Product result = this.productService.update(updatedProduct);

        assertNull(result);
    }

    @Test
    void testDeleteExistingProduct() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Product Name");
        product.setProductQuantity(10);
        this.productService.create(product);

        Product result = this.productService.delete("product-1");

        assertNotNull(result);
        assertEquals("product-1", result.getProductId());
        assertTrue(this.productService.findAll().isEmpty());
    }

    @Test
    void testDeleteMissingProduct() {
        Product result = this.productService.delete("missing-product");

        assertNull(result);
    }
}
