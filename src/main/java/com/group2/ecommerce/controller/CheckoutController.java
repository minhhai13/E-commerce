package com.group2.ecommerce.controller;

import com.group2.ecommerce.entity.Order;
import com.group2.ecommerce.dto.AddressRequest;
import com.group2.ecommerce.dto.AddressResponse;
import com.group2.ecommerce.dto.CartItem;
import com.group2.ecommerce.dto.coupon.CouponResponse;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.service.AddressService;
import com.group2.ecommerce.service.CartService;
import com.group2.ecommerce.service.CouponService;
import com.group2.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final AddressService addressService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final HttpSession session;

    private Long getCurrentUserId() {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) return user.getId();
        throw new IllegalStateException("User not logged in");
    }

    // --- MAIN CHECKOUT PAGE ---
    @GetMapping
    public String showCheckout(Model model, RedirectAttributes rt) {
        try {
            Long userId = getCurrentUserId();
            List<CartItem> cartItems = cartService.getCart(session);
            
            if (cartItems == null || cartItems.isEmpty()) {
                rt.addFlashAttribute("errorMessage", "Your cart is empty. Please add items.");
                return "redirect:/cart";
            }

            model.addAttribute("cartItems", cartItems);
            BigDecimal subtotal = cartService.calculateTotal(cartItems);
            model.addAttribute("subtotal", subtotal);

            // Fetch Addresses
            List<AddressResponse> addresses = addressService.getAddressesByUserId(userId);
            model.addAttribute("hasAddresses", !addresses.isEmpty());

            // Determine Selected Address
            Long selectedAddressId = (Long) session.getAttribute("checkoutSelectedAddressId");
            AddressResponse selectedAddress = null;

            if (!addresses.isEmpty()) {
                if (selectedAddressId != null) {
                    selectedAddress = addresses.stream()
                            .filter(a -> a.getId().equals(selectedAddressId))
                            .findFirst()
                            .orElse(null);
                }
                if (selectedAddress == null) {
                    selectedAddress = addresses.stream()
                            .filter(AddressResponse::isDefault)
                            .findFirst()
                            .orElse(addresses.get(0));
                    session.setAttribute("checkoutSelectedAddressId", selectedAddress.getId());
                }
            }
            model.addAttribute("selectedAddress", selectedAddress);

            // Coupon
            String couponCode = (String) session.getAttribute("checkoutCouponCode");
            BigDecimal discountAmount = BigDecimal.ZERO;

            if (couponCode != null && !couponCode.trim().isEmpty()) {
                try {
                    CouponResponse coupon = couponService.validateCoupon(couponCode);
                    if (coupon.getDiscountType().name().equals("PERCENTAGE")) {
                        discountAmount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
                    } else {
                        discountAmount = coupon.getDiscountValue();
                    }
                    if (discountAmount.compareTo(subtotal) > 0) {
                        discountAmount = subtotal;
                    }
                    model.addAttribute("appliedCouponCode", couponCode);
                    model.addAttribute("discountAmount", discountAmount);
                } catch (Exception e) {
                    session.removeAttribute("checkoutCouponCode");
                    model.addAttribute("couponError", e.getMessage());
                }
            } else {
                model.addAttribute("discountAmount", discountAmount);
            }

            BigDecimal total = subtotal.subtract(discountAmount);
            model.addAttribute("total", total);

            return "checkout";
        } catch (IllegalStateException e) {
            return "redirect:/login";
        } catch (Exception e) {
            rt.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam("couponCode") String couponCode, RedirectAttributes rt) {
        try {
            if (couponCode == null || couponCode.trim().isEmpty()) {
                session.removeAttribute("checkoutCouponCode");
                rt.addFlashAttribute("successMessage", "Coupon removed.");
                return "redirect:/checkout";
            }
            CouponResponse coupon = couponService.validateCoupon(couponCode);
            session.setAttribute("checkoutCouponCode", coupon.getCode());
            rt.addFlashAttribute("successMessage", "Coupon applied successfully.");
        } catch (Exception e) {
            rt.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(RedirectAttributes rt) {
        try {
            Long userId = getCurrentUserId();
            Long addressId = (Long) session.getAttribute("checkoutSelectedAddressId");
            String couponCode = (String) session.getAttribute("checkoutCouponCode");

            if (addressId == null) {
                rt.addFlashAttribute("errorMessage", "Please select or add a shipping address.");
                return "redirect:/checkout";
            }

            Order savedOrder = orderService.createOrder(userId, addressId, couponCode, session);
            
            // Clean up checkout session
            session.removeAttribute("checkoutSelectedAddressId");
            session.removeAttribute("checkoutCouponCode");

            rt.addAttribute("orderId", savedOrder.getId());
            return "redirect:/checkout/success";
        } catch (Exception e) {
            rt.addFlashAttribute("errorMessage", "Could not place order: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @Value("${sepay.bank-acc}")
    private String bankAcc;

    @Value("${sepay.bank-id}")
    private String bankId;

    @GetMapping("/success")
    public String showSuccess(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderService.findOrderById(orderId);

        if (order != null && order.getStatus() == com.group2.ecommerce.entity.enums.OrderStatus.WAITING_PAYMENT) {
            String qrUrl = String.format("https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%s&des=DH%s",
                    bankAcc, bankId, order.getTotalAmount().longValue(), order.getId());
            model.addAttribute("qrUrl", qrUrl);
        }

        model.addAttribute("orderId", orderId);
        return "checkout-success";
    }

    // --- ADDRESS MANAGEMENT FOR CHECKOUT ---

    @GetMapping("/addresses")
    public String showAddresses(Model model) {
        try {
            Long userId = getCurrentUserId();
            List<AddressResponse> addresses = addressService.getAddressesByUserId(userId);
            model.addAttribute("addresses", addresses);
            Long selectedAddressId = (Long) session.getAttribute("checkoutSelectedAddressId");
            model.addAttribute("selectedAddressId", selectedAddressId);
            return "checkout-addresses";
        } catch (IllegalStateException e) {
            return "redirect:/login";
        }
    }

    @PostMapping("/addresses/select")
    public String selectAddress(@RequestParam("addressId") Long addressId) {
        session.setAttribute("checkoutSelectedAddressId", addressId);
        return "redirect:/checkout";
    }

    @GetMapping("/addresses/add")
    public String showAddAddressForm(Model model) {
        if (!model.containsAttribute("addressForm")) {
            model.addAttribute("addressForm", new AddressRequest());
        }
        return "checkout-address-form";
    }

    @PostMapping("/addresses/add")
    public String addAddress(@Valid @ModelAttribute("addressForm") AddressRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes rt) {
        if (bindingResult.hasErrors()) {
            rt.addFlashAttribute("org.springframework.validation.BindingResult.addressForm", bindingResult);
            rt.addFlashAttribute("addressForm", request);
            return "redirect:/checkout/addresses/add";
        }
        try {
            AddressResponse newAddr = addressService.addAddress(getCurrentUserId(), request);
            session.setAttribute("checkoutSelectedAddressId", newAddr.getId());
            rt.addFlashAttribute("successMessage", "Address added successfully.");
            return "redirect:/checkout/addresses";
        } catch (Exception e) {
            rt.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/checkout/addresses/add";
        }
    }

    @GetMapping("/addresses/{id}/edit")
    public String showEditAddressForm(@PathVariable("id") Long id, Model model, RedirectAttributes rt) {
        try {
            Long userId = getCurrentUserId();
            // Fetch address details to populate form
            List<AddressResponse> addresses = addressService.getAddressesByUserId(userId);
            AddressResponse addr = addresses.stream().filter(a -> a.getId().equals(id)).findFirst()
                    .orElseThrow(() -> new RuntimeException("Address not found"));
            
            if (!model.containsAttribute("addressForm")) {
                AddressRequest form = AddressRequest.builder()
                        .recipientName(addr.getRecipientName())
                        .phone(addr.getPhone())
                        .addressDetail(addr.getAddressDetail())
                        .build();
                model.addAttribute("addressForm", form);
            }
            model.addAttribute("addressId", id);
            return "checkout-address-form";
        } catch (Exception e) {
            rt.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/checkout/addresses";
        }
    }

    @PostMapping("/addresses/{id}/edit")
    public String editAddress(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("addressForm") AddressRequest request,
                              BindingResult bindingResult,
                              RedirectAttributes rt) {
        if (bindingResult.hasErrors()) {
            rt.addFlashAttribute("org.springframework.validation.BindingResult.addressForm", bindingResult);
            rt.addFlashAttribute("addressForm", request);
            return "redirect:/checkout/addresses/" + id + "/edit";
        }
        try {
            addressService.updateAddress(getCurrentUserId(), id, request);
            rt.addFlashAttribute("successMessage", "Address updated successfully.");
            return "redirect:/checkout/addresses";
        } catch (Exception e) {
            rt.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/checkout/addresses/" + id + "/edit";
        }
    }

    @PostMapping("/addresses/{id}/delete")
    public String deleteAddress(@PathVariable("id") Long id, RedirectAttributes rt) {
        try {
            addressService.deleteAddress(getCurrentUserId(), id);
            
            // if deleted address was selected in checkout, remove it
            Long selectedId = (Long) session.getAttribute("checkoutSelectedAddressId");
            if (selectedId != null && selectedId.equals(id)) {
                session.removeAttribute("checkoutSelectedAddressId");
            }
            
            rt.addFlashAttribute("successMessage", "Address deleted successfully.");
        } catch (Exception e) {
            rt.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/checkout/addresses";
    }

    @PostMapping("/addresses/{id}/default")
    public String setDefaultAddress(@PathVariable("id") Long id, RedirectAttributes rt) {
        try {
            addressService.setDefaultAddress(getCurrentUserId(), id);
            rt.addFlashAttribute("successMessage", "Set as default successfully.");
        } catch (Exception e) {
            rt.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/checkout/addresses";
    }
}
