package com.example.WebBanSach.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // Mã code ví dụ: "SALE20"

    private int discountPercent; // Giảm bao nhiêu % 

    private Double maxDiscount; // Giảm tối đa bao nhiêu tiền

    private LocalDate expiryDate; // Ngày hết hạn
}