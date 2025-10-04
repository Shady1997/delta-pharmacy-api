package org.pharmacy.api.controller;

import org.pharmacy.api.dto.ApiResponse;
import org.pharmacy.api.dto.ProductRequest;
import org.pharmacy.api.dto.StockUpdateRequest;
import org.pharmacy.api.model.Product;
import org.pharmacy.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.ok(ApiResponse.success("Product created successfully", product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    @GetMapping("/inventory/stock-levels")
    public ResponseEntity<ApiResponse<List<Product>>> getStockLevels() {
        List<Product> lowStockProducts = productService.getLowStockProducts();
        return ResponseEntity.ok(ApiResponse.success(lowStockProducts));
    }

    @PostMapping("/inventory/update-stock")
    public ResponseEntity<ApiResponse<Product>> updateStock(@Valid @RequestBody StockUpdateRequest request) {
        Product product = productService.updateStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", product));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String filter) {

        if (query != null && !query.isEmpty()) {
            List<Product> products = productService.searchProducts(query);
            return ResponseEntity.ok(ApiResponse.success(products));
        }

        if ("prescription_required".equals(filter)) {
            List<Product> products = productService.getProductsByPrescriptionRequired(true);
            return ResponseEntity.ok(ApiResponse.success(products));
        }

        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}
