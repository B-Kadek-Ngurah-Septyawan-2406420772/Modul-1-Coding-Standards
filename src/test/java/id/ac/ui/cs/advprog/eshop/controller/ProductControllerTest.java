package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import id.ac.ui.cs.advprog.eshop.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
class ProductControllerTest {

    ProductController productController;
    ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new ProductRepository();
        this.productService = new ProductServiceImpl();
        ReflectionTestUtils.setField(this.productService, "productRepository", productRepository);
        this.productController = new ProductController();
        ReflectionTestUtils.setField(this.productController, "service", this.productService);
    }

    @Test
    void testEditProductPageExistingProduct() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Product Name");
        product.setProductQuantity(10);
        this.productService.create(product);
        Model model = new ExtendedModelMap();

        String viewName = this.productController.editProductPage("product-1", model);

        assertEquals("editProduct", viewName);
        assertEquals(product, model.getAttribute("product"));
    }

    @Test
    void testEditProductPageMissingProduct() {
        Model model = new ExtendedModelMap();

        String viewName = this.productController.editProductPage("missing-product", model);

        assertEquals("redirect:list", viewName);
    }

    @Test
    void testEditProductPostMissingProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setProductId("missing-product");

        String viewName = this.productController.editProductPost(updatedProduct, new ExtendedModelMap());

        assertEquals("redirect:list", viewName);
    }

    @Test
    void testDeleteProductPostExistingProduct() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Product Name");
        product.setProductQuantity(10);
        this.productService.create(product);

        String viewName = this.productController.deleteProductPost("product-1");

        assertEquals("redirect:/product/list", viewName);
        assertTrue(this.productService.findAll().isEmpty());
    }

    @Test
    void testDeleteProductPostMissingProduct() {
        String viewName = this.productController.deleteProductPost("missing-product");

        assertEquals("redirect:/product/list", viewName);
    }
}
