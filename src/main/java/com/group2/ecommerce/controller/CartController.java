package com.group2.ecommerce.controller;

import com.group2.ecommerce.dto.CartItem;
import com.group2.ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // ─── VIEW CART ───
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = cartService.getCart(session);
        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", cartService.calculateTotal(cart));
        return "cart";
    }

    // ─── ADD TO CART ───
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                           @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                           HttpSession session,
                           RedirectAttributes ra) {
        try {
            cartService.addToCart(productId, quantity, session);
            ra.addFlashAttribute("success", "Item added to cart!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + productId;
        }
        return "redirect:/cart";
    }

    // ─── UPDATE QUANTITY ───
    @PostMapping("/update")
    public String updateCart(@RequestParam("productId") Long productId,
                            @RequestParam("quantity") int quantity,
                            HttpSession session,
                            RedirectAttributes ra) {
        try {
            cartService.updateQuantity(productId, quantity, session);
            ra.addFlashAttribute("success", "Cart updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    // ─── REMOVE ITEM ───
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("productId") Long productId,
                                HttpSession session,
                                RedirectAttributes ra) {
        cartService.removeFromCart(productId, session);
        ra.addFlashAttribute("success", "Item removed from cart.");
        return "redirect:/cart";
    }
}
