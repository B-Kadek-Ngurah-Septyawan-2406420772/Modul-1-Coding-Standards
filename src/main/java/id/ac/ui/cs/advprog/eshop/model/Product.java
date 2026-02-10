package id.ac.ui.cs.advprog.eshop.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Product {
    private String productId;

    @NotBlank(message = "Product name is required.")
    private String productName;

    @Min(value = 1, message = "Product quantity must be at least 1.")
    private int productQuantity;
}
