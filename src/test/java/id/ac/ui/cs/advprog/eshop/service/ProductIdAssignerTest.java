package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ProductIdAssignerTest {

    @Test
    void testAssignUsesFirstSupportingStrategy() {
        Product product = new Product();
        ProductIdGenerationStrategy firstStrategy = mock(ProductIdGenerationStrategy.class);
        ProductIdGenerationStrategy secondStrategy = mock(ProductIdGenerationStrategy.class);
        when(firstStrategy.supports(product)).thenReturn(false);
        when(secondStrategy.supports(product)).thenReturn(true);
        when(secondStrategy.generate(product)).thenReturn("product-2");

        ProductIdAssigner productIdAssigner = new ProductIdAssigner(List.of(firstStrategy, secondStrategy));

        String result = productIdAssigner.assign(product);

        assertEquals("product-2", result);
        verify(firstStrategy).supports(product);
        verify(secondStrategy).supports(product);
        verify(secondStrategy).generate(product);
    }

    @Test
    void testAssignThrowsWhenProductIsNull() {
        ProductIdAssigner productIdAssigner = new ProductIdAssigner(List.of());

        assertThrows(NullPointerException.class, () -> productIdAssigner.assign(null));
    }

    @Test
    void testAssignThrowsWhenNoStrategyCanHandleProduct() {
        Product product = new Product();
        ProductIdGenerationStrategy unsupportedStrategy = mock(ProductIdGenerationStrategy.class);
        when(unsupportedStrategy.supports(product)).thenReturn(false);

        ProductIdAssigner productIdAssigner = new ProductIdAssigner(List.of(unsupportedStrategy));

        assertThrows(IllegalStateException.class, () -> productIdAssigner.assign(product));
        verify(unsupportedStrategy).supports(product);
        verifyNoMoreInteractions(unsupportedStrategy);
    }
}
