package com.group2.ecommerce.controller;

import com.group2.ecommerce.entity.Product;
import com.group2.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping("/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model) {

        Optional<Product> productOpt = productRepository.findById(id);

        if (productOpt.isPresent() && productOpt.get().isActive()) {
            Product product = productOpt.get(); // Extract product for easier access
            model.addAttribute("product", product);

            // Fetch some related products (e.g., from the same category)
            // Just picking 4 from the same category for the "You might also like" section
            // Fetch related products (e.g., from the same category, up to 4 items)
            if (product.getCategory() != null) {
                List<Product> relatedProducts = productRepository.findByCategoryIdAndIsActiveTrue(
                        product.getCategory().getId(),
                        PageRequest.of(0, 5) // Fetch 5 to allow for filtering out the current product
                ).getContent()
                        .stream()
                        .filter(p -> !p.getId().equals(id))
                        .limit(4)
                        .toList();
                model.addAttribute("relatedProducts", relatedProducts);
            }

            return "product-detail";
        } else {
            // Add a proper error page mapping or redirect in the future, for now redirect
            // to home if invalid
            return "redirect:/";
        }
    }
}
