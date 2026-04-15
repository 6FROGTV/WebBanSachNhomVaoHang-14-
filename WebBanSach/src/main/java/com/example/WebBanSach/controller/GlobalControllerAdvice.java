package com.example.WebBanSach.controller;

import com.example.WebBanSach.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // Chú thích này giúp đoạn code bên dưới chạy ngầm trên MỌI trang
public class GlobalControllerAdvice {

    @Autowired
    private CategoryRepository categoryRepository;

    // Tự động nhét danh sách categories vào Model của mọi trang web
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
    }
}