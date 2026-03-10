package com.group2.ecommerce.controller;

import com.group2.ecommerce.entity.Category;
import com.group2.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    // ─── List ────────────────────────────────
    @GetMapping
    public String listCategories(@RequestParam(defaultValue = "") String q,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model) {
        q = q.trim();
        Page<Category> categoriesPage = categoryService.getCategories(q, page);
        model.addAttribute("categoriesPage", categoriesPage);
        model.addAttribute("q", q);
        model.addAttribute("currentPage", page);
        model.addAttribute("activePage", "categories");
        return "admin/categories/category-list";
    }

    // ─── Form (Create & Edit) ─────────────────
    @GetMapping("/form")
    public String showForm(@RequestParam(required = false) Long id, Model model) {
        String name = "";
        String description = "";
        if (id != null) {
            Category category = categoryService.findById(id);
            name = category.getName();
            description = category.getDescription() != null ? category.getDescription() : "";
        }
        model.addAttribute("categoryId", id);
        model.addAttribute("name", name);
        model.addAttribute("description", description);
        model.addAttribute("activePage", "categories");
        model.addAttribute("formTitle", id == null ? "Add Category" : "Edit Category #" + id);
        return "admin/categories/category-form";
    }

    // ─── Save (Create or Update) ──────────────
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long categoryId,
                       @RequestParam String name,
                       @RequestParam(required = false) String description,
                       Model model,
                       RedirectAttributes ra) {
        try {
            categoryService.save(categoryId, name, description);
            ra.addFlashAttribute("success",
                    categoryId == null ? "Category created successfully." : "Category updated successfully.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categoryId", categoryId);
            model.addAttribute("name", name);
            model.addAttribute("description", description);
            model.addAttribute("activePage", "categories");
            model.addAttribute("formTitle", categoryId == null ? "Add Category" : "Edit Category #" + categoryId);
            return "admin/categories/category-form";
        }
        return "redirect:/admin/categories";
    }

    // ─── Toggle Status ───────────────────────
    @PostMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable Long id,
                               @RequestParam(defaultValue = "") String q,
                               @RequestParam(defaultValue = "0") int page,
                               RedirectAttributes ra) {
        try {
            categoryService.toggleStatus(id);
            ra.addFlashAttribute("success", "Category status updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/categories?q=" + q + "&page=" + page;
    }
}
