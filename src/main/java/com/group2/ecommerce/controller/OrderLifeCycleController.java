package com.group2.ecommerce.controller;

import com.group2.ecommerce.entity.Order;
import com.group2.ecommerce.entity.User;
import com.group2.ecommerce.entity.enums.Role;
import com.group2.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class OrderLifeCycleController {
    @Autowired
    private OrderService orderService;


    @GetMapping({"", "/", "/dashboard"})
    String toDashboard(Model model, HttpSession ses){
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF){
            return "error/403";
        }
        orderService.countAll();
        return "order-lifecycle/order-dashboard";
    }
    @GetMapping("/list")
    String toList(Model model, HttpSession ses){
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF){
            return "error/403";
        }
        List<Order> orderList = orderService.fetchOrderList();
        model.addAttribute("order_list",orderList);
        return "order-lifecycle/order-list";
    }

    @GetMapping("/{id}")
    String viewOrderDetails(@PathVariable Long id, Model model, HttpSession ses, RedirectAttributes redirectAttributes){
        String status;
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF){
            return "error/403";
        }
        Order order = orderService.findOrderById(id);
        if(order == null){
            status = "Đơn hàng có id #" + id + " không tồn tại.";
            redirectAttributes.addFlashAttribute("status", status);
            return "redirect:/staff/list";
        }
        model.addAttribute("order",order);
        return "order-lifecycle/order-detail";
    }

    @PostMapping("/{id}/advance")
    String advanceStatus(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession ses){
        String status;
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF){
            return "error/403";
        }
        boolean changeStatusSuccess = orderService.advanceOrderStatus(id);
        status = changeStatusSuccess?"Trạng thái đơn hàng đã được cập nhật thành công.":"Chuyển trạng thái đơn hàng thất bại. Trạng thái không hợp lệ.";
        redirectAttributes.addFlashAttribute("status",status);
        return "redirect:/staff/" + id;
    }

    @PostMapping("/{id}/cancelled")
    String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession ses){
        String status;
        User user = (User) ses.getAttribute("loggedInUser");
        if(user.getRole()!= Role.STAFF){
            return "error/403";
        }
        boolean cancelOrderSuccess = orderService.cancelOrder(id);
        status = cancelOrderSuccess?"Hủy đơn hàng thành công. Số lượng tồn kho đã được cập nhật lại.":"Không thể hủy đơn hàng. Đơn có thể đã được giao cho đơn vị vận chuyển.";
        redirectAttributes.addFlashAttribute("status",status);
        return "redirect:/staff/" + id;
    }


}
