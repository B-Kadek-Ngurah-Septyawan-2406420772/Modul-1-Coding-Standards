package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductMutationService;
import id.ac.ui.cs.advprog.eshop.service.ProductQueryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductMutationService productMutationService;
    private final ProductQueryService productQueryService;

    public ProductController(ProductMutationService productMutationService, ProductQueryService productQueryService) {
        this.productMutationService = productMutationService;
        this.productQueryService = productQueryService;
    }

    @GetMapping("/create")
    public String createProductPage (Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "createProduct";
    }

    @PostMapping("/create")
    public String createProductPost (@Valid @ModelAttribute Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "createProduct";
        }
        productMutationService.create(product);
        return "redirect:list";
    }

    @GetMapping("/list")
    public String productListPage (Model model) {
        List<Product> allProducts = productQueryService.findAll();
        model.addAttribute("products", allProducts);
        return "productList";
    }

    @GetMapping("/edit/{productId}")
    public String editProductPage (@PathVariable String productId, Model model) {
        Product product = productQueryService.findById(productId);
        if (product == null) {
            return "redirect:list";
        }
        model.addAttribute("product", product);
        return "editProduct";
    }

    @PostMapping("/edit")
    public String editProductPost (@Valid @ModelAttribute Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "editProduct";
        }
        Product updatedProduct = productMutationService.update(product);
        if (updatedProduct == null) {
            return "redirect:list";
        }
        return "redirect:list";
    }

    @PostMapping("/delete/{productId}")
    public String deleteProductPost (@PathVariable String productId) {
        Product deletedProduct = productMutationService.delete(productId);
        if (deletedProduct == null) {
            return "redirect:/product/list";
        }
        return "redirect:/product/list";
    }

    @GetMapping("/delete/{productId}")
    public String deleteProductGet (@PathVariable String productId) {
        return "redirect:/product/list";
    }
}
