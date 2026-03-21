package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.product.ProductRequest;
import com.group2.ecommerce.dto.product.ProductResponse;
import com.group2.ecommerce.entity.Category;
import com.group2.ecommerce.entity.Product;
import com.group2.ecommerce.service.CategoryService;
import com.group2.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // ─── List ────────────────────────────────
    @GetMapping
    public String listProducts(@RequestParam(name = "q", defaultValue = "") String q,
                               @RequestParam(name = "categoryId", required = false) Long categoryId,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               Model model) {
        q = q.trim();
        Page<ProductResponse> productsPage = productService.getProducts(q.isEmpty() ? null : q, categoryId, page);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("productsPage", productsPage);
        model.addAttribute("categories", categories);
        model.addAttribute("q", q);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("currentPage", page);
        model.addAttribute("activePage", "products");
        return "admin/products/product-list";
    }

    // ─── Form (Create & Edit) ─────────────────
    @GetMapping("/form")
    public String showForm(@RequestParam(name = "id", required = false) Long id, Model model) {
        ProductRequest request = new ProductRequest();
        if (id != null) {
            ProductResponse product = productService.findById(id);
            request.setName(product.getName());
            request.setDescription(product.getDescription());
            request.setPrice(product.getPrice());
            request.setStockQuantity(product.getStockQuantity());
            request.setImageName(product.getImageName());
            if (product.getCategoryId() != null) {
                request.setCategoryId(product.getCategoryId());
            }
        }
        List<Category> categories = categoryService.findAll();
        model.addAttribute("productId", id);
        model.addAttribute("productRequest", request);
        model.addAttribute("categories", categories);
        model.addAttribute("activePage", "products");
        model.addAttribute("formTitle", id == null ? "Add Product" : "Edit Product #" + id);
        return "admin/products/product-form";
    }

    // ─── Save (Create or Update) ──────────────
    @PostMapping("/save")
    public String save(@RequestParam(name = "productId", required = false) Long productId,
                       @Valid @ModelAttribute("productRequest") ProductRequest request,
                       BindingResult result,
                       Model model,
                       RedirectAttributes ra) {
        if (result.hasErrors()) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("productId", productId);
            model.addAttribute("categories", categories);
            model.addAttribute("activePage", "products");
            model.addAttribute("formTitle", productId == null ? "Add Product" : "Edit Product #" + productId);
            return "admin/products/product-form";
        }
        try {
            productService.save(productId, request);
            ra.addFlashAttribute("success",
                    productId == null ? "Product created successfully." : "Product updated successfully.");
        } catch (IllegalArgumentException e) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("error", e.getMessage());
            model.addAttribute("productId", productId);
            model.addAttribute("categories", categories);
            model.addAttribute("activePage", "products");
            model.addAttribute("formTitle", productId == null ? "Add Product" : "Edit Product #" + productId);
            return "admin/products/product-form";
        } catch (IOException e) {
            List<Category> categories = categoryService.findAll();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("productId", productId);
            model.addAttribute("categories", categories);
            model.addAttribute("activePage", "products");
            model.addAttribute("formTitle", productId == null ? "Add Product" : "Edit Product #" + productId);
            return "admin/products/product-form";
        }
        return "redirect:/admin/products";
    }

    // ─── Toggle Status ───────────────────────
    @PostMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable("id") Long id,
                               @RequestParam(name = "q", defaultValue = "") String q,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               RedirectAttributes ra) {
        try {
            productService.toggleStatus(id);
            ra.addFlashAttribute("success", "Product status updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/products?q=" + q + "&page=" + page;
    }
}
