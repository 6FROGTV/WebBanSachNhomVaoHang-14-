package com.example.WebBanSach.controller;

import com.example.WebBanSach.model.User; 
import com.example.WebBanSach.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Bổ sung thư viện này

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("newUser", new User()); 
        return "admin/user-list"; 
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("newUser") User user, RedirectAttributes ra) { 
        try {
            userRepository.save(user); 
            return "redirect:/admin/users?success=true"; 
        } catch (Exception e) {
            // Nếu trùng Email hoặc Username, DB sẽ báo lỗi. Mình bắt lỗi ở đây:
            ra.addFlashAttribute("errorMessage", "Lỗi: Tên tài khoản hoặc Email này đã tồn tại trong hệ thống!");
            return "redirect:/admin/users";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userRepository.deleteById(id);
            return "redirect:/admin/users?success=true";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: Không thể xóa tài khoản này!");
            return "redirect:/admin/users";
        }
    }
}