package com.example.WebBanSach.controller;

import com.example.WebBanSach.model.Book;
import com.example.WebBanSach.model.CartItem;
import com.example.WebBanSach.repository.BookRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cartItems");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        
        double totalPrice = 0;
        for (CartItem item : cart) {
            totalPrice += item.getSubtotal();
        }
        
        model.addAttribute("cartItems", cart);
        model.addAttribute("totalPrice", totalPrice);
        return "cart"; 
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("bookId") Long bookId, 
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity, 
                            HttpSession session, 
                            RedirectAttributes ra) {
        
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return "redirect:/";

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cartItems");
        if (cart == null) cart = new ArrayList<>();

        // Kiểm tra xem sách này đã có sẵn bao nhiêu cuốn trong giỏ
        boolean exists = false;
        int currentQty = 0;
        for (CartItem item : cart) {
            if (item.getBookId().equals(bookId)) {
                currentQty = item.getQuantity();
                exists = true;
                break;
            }
        }

        // RÀNG BUỘC 1: Chặn thêm nếu tổng số lượng vượt quá Tồn kho
        if (currentQty + quantity > book.getStock()) {
            ra.addFlashAttribute("errorMessage", "Rất tiếc, sách '" + book.getTitle() + "' chỉ còn " + book.getStock() + " cuốn trong kho!");
            return "redirect:/cart"; // Đẩy sang giỏ hàng để xem cảnh báo
        }

        if (exists) {
            for (CartItem item : cart) {
                if (item.getBookId().equals(bookId)) {
                    item.setQuantity(currentQty + quantity);
                    break;
                }
            }
        } else {
            CartItem newItem = new CartItem();
            newItem.setBookId(book.getId());
            newItem.setTitle(book.getTitle());
            newItem.setImageUrl(book.getImageUrl());
            
            // Lấy đúng giá khuyến mãi
            newItem.setPrice(book.getSalePrice()); 
            newItem.setQuantity(quantity);
            cart.add(newItem);
        }

        session.setAttribute("cartItems", cart);
        updateCartCount(session, cart);
        
        ra.addFlashAttribute("message", "Đã thêm sách vào giỏ hàng thành công!");
        return "redirect:/"; 
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam("bookId") Long bookId, 
                             @RequestParam("quantity") int quantity, 
                             HttpSession session,
                             RedirectAttributes ra) { 
        
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) return "redirect:/cart";

        // RÀNG BUỘC 2: Chặn khách cố tình gõ số 1000 trong trang giỏ hàng
        if (quantity > book.getStock()) {
            ra.addFlashAttribute("errorMessage", "Sách '" + book.getTitle() + "' hiện chỉ còn tối đa " + book.getStock() + " cuốn!");
            quantity = book.getStock(); // Ép số lượng về mức tồn kho cao nhất
        } else if (quantity < 1) {
            quantity = 1; // Không cho gõ số âm
        }

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cartItems");
        if (cart != null) {
            for (CartItem item : cart) {
                if (item.getBookId().equals(bookId)) {
                    item.setQuantity(quantity); 
                    break;
                }
            }
            session.setAttribute("cartItems", cart);
            updateCartCount(session, cart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long bookId, HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cartItems");
        if (cart != null) {
            cart.removeIf(item -> item.getBookId().equals(bookId));
            session.setAttribute("cartItems", cart);
            updateCartCount(session, cart);
        }
        return "redirect:/cart";
    }

    private void updateCartCount(HttpSession session, List<CartItem> cart) {
        int totalItems = 0;
        for (CartItem item : cart) {
            totalItems += item.getQuantity();
        }
        session.setAttribute("cartCount", totalItems);
    }
}