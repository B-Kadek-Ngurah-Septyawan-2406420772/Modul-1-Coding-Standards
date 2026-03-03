package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;

public interface ProductMutationService {
    Product create(Product product);

    Product update(Product product);

    Product delete(String productId);
}
