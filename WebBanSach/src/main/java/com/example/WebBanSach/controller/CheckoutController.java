package com.example.WebBanSach.controller;

import com.example.WebBanSach.model.*;
import com.example.WebBanSach.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;

    @GetMapping
    public String showCheckoutPage(Model model, Principal principal, HttpSession session) {
        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null || cartItems.isEmpty()) return "redirect:/cart";
        if (principal == null) return "redirect:/login"; 

        double totalPrice = 0;
        for (CartItem item : cartItems) totalPrice += item.getSubtotal();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("order", new Order()); 
        return "checkout";
    }

    @PostMapping("/process")
    public String processOrder(@ModelAttribute("order") Order order, Principal principal, HttpSession session, RedirectAttributes ra) {
        if (principal == null) return "redirect:/login";

        List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
        if (cartItems == null || cartItems.isEmpty()) return "redirect:/cart";

        String username = principal.getName();
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
            username = oauthToken.getPrincipal().getAttribute("email"); 
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user!"));

        double totalPrice = 0;
        for (CartItem item : cartItems) totalPrice += item.getSubtotal();

        order.setUser(user);
        order.setTotalAmount(totalPrice);
        order.setCreatedAt(LocalDateTime.now());
        
        // Đặt trạng thái ban đầu dựa vào phương thức
        if ("COD".equals(order.getPaymentMethod())) {
            order.setStatus("Chờ xác nhận");
        } else {
            order.setStatus("Chờ thanh toán");
        }

        List<OrderDetail> details = new ArrayList<>();
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            Book book = bookRepository.findById(item.getBookId()).orElse(null);
            detail.setBook(book);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            details.add(detail);

            if(book != null) {
                book.setStock(book.getStock() - item.getQuantity());
                bookRepository.save(book);
            }
        }
        order.setOrderDetails(details);
        orderRepository.save(order);

        // Xóa giỏ hàng
        session.removeAttribute("cartItems");
        session.setAttribute("cartCount", 0);

        // ĐIỀU HƯỚNG: Nếu COD thì qua trang Thành công, nếu CK thì qua trang Quét QR
        if ("COD".equals(order.getPaymentMethod())) {
            ra.addFlashAttribute("orderId", order.getId());
            return "redirect:/checkout/success";
        } else {
            return "redirect:/checkout/pay?id=" + order.getId();
        }
    }

    // TẠO TRANG HIỂN THỊ MÃ QR MỚI (Giống pay_qr.php của bạn)
    @GetMapping("/pay")
    public String showQrPage(@RequestParam("id") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || "COD".equals(order.getPaymentMethod())) {
            return "redirect:/";
        }
        model.addAttribute("order", order);
        return "pay-qr"; 
    }

    // XỬ LÝ NÚT "TÔI ĐÃ THANH TOÁN" (Giống pay_confirm.php của bạn)
    @GetMapping("/confirm-pay")
    public String confirmPayment(@RequestParam("id") Long orderId, RedirectAttributes ra) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && "Chờ thanh toán".equals(order.getStatus())) {
            order.setStatus("Đã thanh toán"); // Cập nhật trạng thái
            orderRepository.save(order);
        }
        ra.addFlashAttribute("orderId", orderId);
        return "redirect:/checkout/success";
    }

    @GetMapping("/success")
    public String orderSuccess() {
        return "order-success"; 
    }
}