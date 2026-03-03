package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import id.ac.ui.cs.advprog.eshop.service.ExistingProductIdGenerationStrategy;
import id.ac.ui.cs.advprog.eshop.service.MissingProductIdGenerationStrategy;
import id.ac.ui.cs.advprog.eshop.service.ProductIdAssigner;
import id.ac.ui.cs.advprog.eshop.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ProductControllerTest {

    ProductController productController;
    ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new ProductRepository();
        ProductIdAssigner productIdAssigner = new ProductIdAssigner(List.of(
                new MissingProductIdGenerationStrategy(),
                new ExistingProductIdGenerationStrategy()
        ));
        this.productService = new ProductServiceImpl(productRepository, productRepository, productIdAssigner);
        this.productController = new ProductController(this.productService, this.productService);
    }

    @Test
    void testCreateProductPage() {
        Model model = new ExtendedModelMap();

        String viewName = this.productController.createProductPage(model);

        assertEquals("createProduct", viewName);
        assertInstanceOf(Product.class, model.getAttribute("product"));
    }

    @Test
    void testCreateProductPostSuccess() {
        Product product = new Product();
        product.setProductName("Product Name");
        product.setProductQuantity(10);
        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");

        String viewName = this.productController.createProductPost(product, bindingResult, new ExtendedModelMap());

        assertEquals("redirect:list", viewName);
        assertEquals(1, this.productService.findAll().size());
        assertNotNull(this.productService.findAll().getFirst().getProductId());
    }

    @Test
    void testCreateProductPostValidationError() {
        Product product = new Product();
        BindingResult bindingResult = new BeanPropertyBindingResult(product, "product");
        bindingResult.rejectValue("productName", "NotBlank", "Product name is required.");

        String viewName = this.productController.createProductPost(product, bindingResult, new ExtendedModelMap());

        assertEquals("createProduct", viewName);
        assertTrue(this.productService.findAll().isEmpty());
    }

    @Test
    void testProductListPage() {
        Product first = new Product();
        first.setProductName("Product A");
        first.setProductQuantity(1);
        this.productService.create(first);

        Product second = new Product();
        second.setProductName("Product B");
        second.setProductQuantity(2);
        this.productService.create(second);

        Model model = new ExtendedModelMap();

        String viewName = this.productController.productListPage(model);

        assertEquals("productList", viewName);
        assertInstanceOf(List.class, model.getAttribute("products"));
        List<?> products = (List<?>) model.getAttribute("products");
        assertNotNull(products);
        assertEquals(2, products.size());
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
        updatedProduct.setProductName("Product Name");
        updatedProduct.setProductQuantity(10);
        BindingResult bindingResult = new BeanPropertyBindingResult(updatedProduct, "product");

        String viewName = this.productController.editProductPost(updatedProduct, bindingResult, new ExtendedModelMap());

        assertEquals("redirect:list", viewName);
    }

    @Test
    void testEditProductPostValidationError() {
        Product updatedProduct = new Product();
        updatedProduct.setProductId("product-1");
        updatedProduct.setProductName("Product Name");
        updatedProduct.setProductQuantity(10);
        BindingResult bindingResult = new BeanPropertyBindingResult(updatedProduct, "product");
        bindingResult.rejectValue("productName", "NotBlank", "Product name is required.");

        String viewName = this.productController.editProductPost(updatedProduct, bindingResult, new ExtendedModelMap());

        assertEquals("editProduct", viewName);
    }

    @Test
    void testEditProductPostExistingProduct() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Old Name");
        product.setProductQuantity(10);
        this.productService.create(product);

        Product updatedProduct = new Product();
        updatedProduct.setProductId("product-1");
        updatedProduct.setProductName("New Name");
        updatedProduct.setProductQuantity(20);
        BindingResult bindingResult = new BeanPropertyBindingResult(updatedProduct, "product");

        String viewName = this.productController.editProductPost(updatedProduct, bindingResult, new ExtendedModelMap());

        assertEquals("redirect:list", viewName);
        Product result = this.productService.findById("product-1");
        assertNotNull(result);
        assertEquals("New Name", result.getProductName());
        assertEquals(20, result.getProductQuantity());
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

    @Test
    void testDeleteProductGet() {
        String viewName = this.productController.deleteProductGet("product-1");

        assertEquals("redirect:/product/list", viewName);
    }
}
