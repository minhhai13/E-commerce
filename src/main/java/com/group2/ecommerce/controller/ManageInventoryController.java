package com.group2.ecommerce.controller;

import com.group2.ecommerce.entity.Category;
import com.group2.ecommerce.entity.Product;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.entity.enums.Role;
import com.group2.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/staff/inventory")
public class ManageInventoryController {
    @Autowired
    private ProductService productService;

    @GetMapping
    private String toList(Model model, HttpSession ses){
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF){
            return "error/403";
        }
        List<Product> productList = productService.fetchAllProduct();
        model.addAttribute("product_list",productList);
        return "inventory-management/inventory-list";
    }
    @GetMapping("/update/{id}")
    private String toUpdateForm(@PathVariable Long id, Model model, HttpSession ses){
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF){
            return "error/403";
        }
        model.addAttribute("id",id);
        return "inventory-management/inventory-form";
    }
    @PostMapping("/delete/{id}")
    private String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession ses){
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF){
            return "error/403";
        }
        String status;
        boolean checkDelete = productService.deleteProduct(id);
        status = checkDelete?"Vô hiệu hóa sản phẩm thành công":"Vô hiệu hóa sản phẩm thất bại";
        redirectAttributes.addFlashAttribute("status",status);
        return "redirect:/staff/inventory";
    }

    @PostMapping("/update/{id}")
    private String updateProduct(@PathVariable Long id, @RequestAttribute("quantity") int quantity,
                                 RedirectAttributes redirectAttributes, HttpSession ses){
        String status;
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF){
            return "error/403";
        }
        boolean checkUpdate = productService.updateProductQuantity(id, quantity);
        status = checkUpdate?"Cập nhật sản phẩm thành công":"Cập nhật sản phẩm thất bại.";
        redirectAttributes.addFlashAttribute("status",status);
        return "redirect:/staff/inventory";
    }
    @PostMapping("/create")
    private String createProduct(
            @RequestAttribute("id") Long id,
            @RequestAttribute("product_name") String productName,
            @RequestAttribute("category") Category category,
            @RequestAttribute("price")BigDecimal price,
            @RequestAttribute("quantity") int quantity,
            @RequestParam("image") MultipartFile image,
            @RequestAttribute("description") String description,
            RedirectAttributes redirectAttributes, HttpSession ses){
        String status;
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF && user.getRole()!= Role.ADMIN){
            return "error/403";
        }
        String imageName = productService.saveImage(image);
        Product product = new Product(id,category,productName,description,price,quantity,imageName,true, LocalDateTime.now());
        boolean checkCreate = productService.createProduct(product);
        status = checkCreate?"True":"False";
        redirectAttributes.addFlashAttribute("status",status);
        return "redirect:/staff/inventory";
    }


}
