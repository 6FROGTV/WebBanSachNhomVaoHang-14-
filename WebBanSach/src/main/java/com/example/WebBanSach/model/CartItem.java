package com.example.WebBanSach.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tác giả: thanh xuân - không vui
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long bookId;
    private String title;
    private String imageUrl;
    private double price;
    private int quantity;

    // Hàm tính thành tiền cho từng món (Đã được gọi ở file cart.html)
    public double getSubtotal() {
        return this.price * this.quantity;
    }
}