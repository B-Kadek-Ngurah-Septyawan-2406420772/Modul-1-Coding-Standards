package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class ProductRepository {
    private List<Product> productData = new ArrayList<>();

    public Product create(Product product) {
        productData.add(product);
        return product;
    }

    public Iterator<Product> findAll() {
        return productData.iterator();
    }

    public Product findById(String productId) {
        if (productId == null) {
            return null;
        }
        for (Product product : productData) {
            if (productId.equals(product.getProductId())) {
                return product;
            }
        }
        return null;
    }

    public Product update(Product product) {
        Product productToUpdate = findById(product.getProductId());
        if (productToUpdate == null) {
            return null;
        }
        productToUpdate.setProductName(product.getProductName());
        productToUpdate.setProductQuantity(product.getProductQuantity());
        return productToUpdate;
    }

    public Product delete(String productId) {
        Product productToDelete = findById(productId);
        if (productToDelete == null) {
            return null;
        }
        productData.remove(productToDelete);
        return productToDelete;
    }
}
