package com.example.WebBanSach.service;

import com.example.WebBanSach.model.Book;
import com.example.WebBanSach.model.CartItem;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope // Đánh dấu bean này sống theo Session của từng User riêng biệt
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();

    // 1. Thêm vào giỏ
    public void addToCart(Book book, int quantity) {
        // Nếu sách đã có trong giỏ, chỉ cần cộng dồn số lượng
        for (CartItem item : cartItems) {
            if (item.getBookId().equals(book.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        // Nếu sách chưa có, tạo mới
        cartItems.add(new CartItem(book.getId(), book.getTitle(), book.getImageUrl(), book.getPrice(), quantity));
    }

    // 2. Lấy danh sách đồ trong giỏ
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    // 3. Cập nhật số lượng
    public void updateQuantity(Long bookId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getBookId().equals(bookId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    // 4. Xóa món đồ
    public void removeItem(Long bookId) {
        cartItems.removeIf(item -> item.getBookId().equals(bookId));
    }

    // 5. Tính tổng tiền cả giỏ
    public double getTotalPrice() {
        return cartItems.stream().mapToDouble(CartItem::getSubtotal).sum();
    }
}