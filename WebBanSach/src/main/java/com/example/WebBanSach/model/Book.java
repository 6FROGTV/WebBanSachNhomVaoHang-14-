package com.example.WebBanSach.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private Double price;
    private int stock;
    private String imageUrl;
    
    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // --- BỔ SUNG TRƯỜNG GIẢM GIÁ Ở ĐÂY ---
    @Column(name = "discount_percent", columnDefinition = "int default 0")
    private int discountPercent = 0;

    // Hàm tự động tính giá sau khi đã trừ % khuyến mãi
    public double getSalePrice() {
        if (discountPercent <= 0) return this.price;
        return this.price * (100 - this.discountPercent) / 100.0;
    }
}