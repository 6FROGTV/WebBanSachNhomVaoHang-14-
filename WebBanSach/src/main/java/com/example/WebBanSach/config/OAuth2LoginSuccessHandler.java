package com.example.WebBanSach.config;

import com.example.WebBanSach.model.User;
import com.example.WebBanSach.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User; // Bổ sung thư viện này
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Success Handler
 * Tác giả: thanh xuân - không vui
 */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            
            // Lấy email từ Google
            String email = oAuth2User.getAttribute("email");

            // Tìm user trong DB, nếu không có thì trả về null
            User dbUser = userRepository.findByUsername(email).orElse(null);

            // XỬ LÝ 1: Cấp quyền và lưu DB nếu là lần đầu đăng nhập
            if (dbUser == null) {
                dbUser = new User();
                dbUser.setUsername(email); 
                dbUser.setEmail(email);
                dbUser.setPassword(passwordEncoder.encode("GOOGLE_SSO_PASSWORD")); 
                
                // Cấp quyền Admin cho đúng email của bạn
                if ("anhhuybui672@gmail.com".equals(email)) {
                    dbUser.setRole("ADMIN");
                } else {
                    dbUser.setRole("USER");
                }
                
                userRepository.save(dbUser);
            }

            // XỬ LÝ 2: Nạp quyền thực tế vào hệ thống bảo mật của Spring
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + dbUser.getRole()));

            // 🚀 ĐÃ SỬA Ở ĐÂY: Ép Spring dùng "name" (Tên thật của Google) làm tên hiển thị thay vì ID
            OAuth2User customUser = new DefaultOAuth2User(
                    authorities,
                    oAuth2User.getAttributes(),
                    "name" // Nếu bạn muốn hiện Email thì đổi chữ "name" thành "email" nhé!
            );

            Authentication newAuth = new OAuth2AuthenticationToken(
                    customUser, 
                    authorities, 
                    oauthToken.getAuthorizedClientRegistrationId()
            );
            
            // Cập nhật lại phiên đăng nhập để hệ thống nhận diện đúng quyền và tên
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            
            // Thành công thì bay về trang chủ
            response.sendRedirect("/");
            
        } catch (Exception e) {
            System.err.println("🚨 LỖI KHI LƯU TÀI KHOẢN GOOGLE: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("/login?error");
        }
    }
}