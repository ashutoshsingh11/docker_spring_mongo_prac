package com.example.crud.service;

import com.example.crud.dto.ProductRequest;
import com.example.crud.dto.ProductResponse;
import com.example.crud.exception.DuplicateResourceException;
import com.example.crud.exception.ResourceNotFoundException;
import com.example.crud.model.Product;
import com.example.crud.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // ── CREATE ──────────────────────────────────────────────────────────────

    public ProductResponse create(ProductRequest request) {
        log.debug("Creating product with name: {}", request.getName());

        if (productRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Product with name '" + request.getName() + "' already exists");
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .category(request.getCategory())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created with id: {}", saved.getId());
        return toResponse(saved);
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    public ProductResponse getById(String id) {
        return toResponse(findOrThrow(id));
    }

    public Page<ProductResponse> getAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    public List<ProductResponse> getByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> getByPriceRange(Double min, Double max) {
        return productRepository.findByPriceBetween(min, max)
                .stream().map(this::toResponse).toList();
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    public ProductResponse update(String id, ProductRequest request) {
        log.debug("Updating product id: {}", id);
        Product existing = findOrThrow(id);

        // check name uniqueness only if name is changing
        if (!existing.getName().equals(request.getName())
                && productRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(
                    "Product with name '" + request.getName() + "' already exists");
        }

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setQuantity(request.getQuantity());
        existing.setCategory(request.getCategory());

        Product updated = productRepository.save(existing);
        log.info("Product updated: {}", id);
        return toResponse(updated);
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    public void delete(String id) {
        log.debug("Deleting product id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted: {}", id);
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private Product findOrThrow(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .category(p.getCategory())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
