package com.example.WebBanSach.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PolicyController {

    // Bắt cái link có dạng /policy/...
    @GetMapping("/policy/{type}")
    public String viewPolicy(@PathVariable String type, Model model) {
        // Ném cái biến 'type' (ví dụ: privacy, shipping...) qua cho HTML xử lý
        model.addAttribute("type", type);
        
        // Trả về file policy.html
        return "policy"; 
    }
}