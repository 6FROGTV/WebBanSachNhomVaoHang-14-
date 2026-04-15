package com.example.WebBanSach.controller;

import com.example.WebBanSach.model.User;
import com.example.WebBanSach.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage(Principal principal) {
        // Nâng cấp: Nếu đã đăng nhập rồi thì đá về trang chủ, không cho vào trang login nữa
        if (principal != null) {
            return "redirect:/";
        }
        return "login"; 
    }

    @GetMapping("/register")
    public String registerPage(Model model, Principal principal) {
        if (principal != null) {
            return "redirect:/";
        }
        model.addAttribute("user", new User());
        return "register"; 
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, Model model) {
        if (userRepository.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "register";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "register";
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER"); 
        
        userRepository.save(user); 
        return "redirect:/login?success"; 
    }
}