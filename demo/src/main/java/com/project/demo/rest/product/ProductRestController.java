package com.project.demo.rest.product;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductRestController {
    @Autowired
    private ProductRepository ProductRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER, SUPER_ADMIN_ROLE')")
    public List<Product> getAllPoducts() {
        return ProductRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Product addProduct(@RequestBody Product product) {
        Optional<Category> selectedCategory = categoryRepository.findById(product.getCategory().getId());
        selectedCategory.ifPresent(product::setCategory);
        return ProductRepository.save(product);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return ProductRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @GetMapping("/filterByName/{name}")
    public List<Product> getProductById(@PathVariable String name) {
        return ProductRepository.findProductWithCharacterInName(name);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ProductRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setStock(product.getStock());
                    return ProductRepository.save(existingProduct);
                })
                .orElseGet(() -> {
                    product.setId(id);
                    return ProductRepository.save(product);
                });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public void deleteProduct(@PathVariable Long id) {
        ProductRepository.deleteById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Product authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Product) authentication.getPrincipal();
    }
}