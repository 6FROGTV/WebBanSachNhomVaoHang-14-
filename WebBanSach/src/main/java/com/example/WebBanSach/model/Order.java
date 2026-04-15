package com.example.WebBanSach.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders") 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double totalAmount;

    private String status; // Ví dụ: "Chờ xác nhận", "Đang giao hàng", "Đã hoàn thành"

    private String paymentMethod; // Ví dụ: "Thanh toán khi nhận hàng (COD)"

    // --- CÁC TRƯỜNG THÔNG TIN GIAO HÀNG VỪA BỔ SUNG ---
    private String fullName;      // Họ tên người nhận
    private String phone;         // Số điện thoại
    private String address;       // Địa chỉ giao hàng
    private String note;          // Ghi chú đơn hàng

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
}