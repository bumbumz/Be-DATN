package com.phamcongvinh.springrestfull.config;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.phamcongvinh.springrestfull.module.Permission;
import com.phamcongvinh.springrestfull.module.Role;
import com.phamcongvinh.springrestfull.module.User;
import com.phamcongvinh.springrestfull.service.TokenService;
import com.phamcongvinh.springrestfull.service.UserService;
import com.phamcongvinh.springrestfull.util.exception.PermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);
        String email = TokenService.getCurrentUserLogin().isPresent()
                ? TokenService.getCurrentUserLogin().get()
                : "";
        System.out.println(">>> email= " + email);
        if (email != null && !email.isEmpty()) {
            Optional<User> user = userService.checkEmail(email);
            if (user.isPresent()) {
                Role role = user.get().getRole();
                if (role != null) {
                    List<Permission> listPer = role.getPermissions();
                    boolean isAllow = listPer.stream()
                            .anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if (isAllow == false) {
                        throw new PermissionException("Bạn không có quyền truy cccccccập");
                    }

                } else {
                    throw new PermissionException("Bạn không có quyền truy cậppppp");
                }
            }

        }

        return true;
    }

}
