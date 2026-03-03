package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;

public interface ProductIdGenerationStrategy {
    boolean supports(Product product);

    String generate(Product product);
}
