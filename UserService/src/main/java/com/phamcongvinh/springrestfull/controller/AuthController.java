package com.phamcongvinh.springrestfull.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phamcongvinh.springrestfull.module.Permission;
import com.phamcongvinh.springrestfull.module.Role;
import com.phamcongvinh.springrestfull.module.User;
import com.phamcongvinh.springrestfull.module.dto.Request.CheckUser;
import com.phamcongvinh.springrestfull.module.dto.Request.RequestLoginDTO;
import com.phamcongvinh.springrestfull.module.dto.Response.AuthResponse;
import com.phamcongvinh.springrestfull.module.dto.Response.UserAuthDTO;
import com.phamcongvinh.springrestfull.service.TokenService;
import com.phamcongvinh.springrestfull.service.UserService;
import com.phamcongvinh.springrestfull.util.exception.AppException;
import com.phamcongvinh.springrestfull.util.exception.PermissionException;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/users")
public class AuthController {
    @Value("${pcv.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            TokenService tokenService,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenService = tokenService;
        this.userService = userService;

    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody RequestLoginDTO loginlogin) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginlogin.getUsername(), loginlogin.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // lưu trữ thông tin người đăng nhập
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthResponse accessToken = new AuthResponse();
        accessToken.setAccessToken(this.tokenService.createAccessToken(authentication.getName()));
        String refreshToken = this.tokenService.createRefreshToken(authentication.getName());
        this.userService.updateRefreshToken(authentication.getName(), refreshToken);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                // .domain("example.com")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(accessToken);
    }

    @GetMapping("/auth/account")
    public ResponseEntity<UserAuthDTO> getMyAcount() {
        String email = TokenService.getCurrentUserLogin().isPresent() ? TokenService.getCurrentUserLogin().get() : "";
        User user = this.userService.checkEmail(email).get();
        UserAuthDTO res = new UserAuthDTO(user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/auth/check-account")
    public boolean checkMyAcount(@RequestBody CheckUser checkUser) {
        String email = TokenService.getCurrentUserLogin().isPresent() ? TokenService.getCurrentUserLogin().get() : "";

        boolean check = true;
        if (email != null && !email.isEmpty()) {
            Optional<User> user = userService.checkEmail(email);
            if (user.isPresent()) {
                Role role = user.get().getRole();
                if (role != null) {
                    List<Permission> listPer = role.getPermissions();
                    boolean isAllow = listPer.stream()
                            .anyMatch(item -> item.getApiPath().equals(checkUser.getPath())
                                    && item.getMethod().equals(checkUser.getHttpMethod()));
                    if (isAllow == false) {
                        check = false;
                    }

                } else {
                    check = false;
                }

            }
        }

        User user = this.userService.checkEmail(email).get();
        UserAuthDTO res = new UserAuthDTO(user.getId(), user.getName(), user.getEmail());
        return check;
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue("refreshToken") String refreshToken) throws AppException {
        Jwt jwt = this.tokenService.checkValidRefeshToken(refreshToken);
        String email = jwt.getSubject();
        Optional<User> user = this.userService.checkEmailAndRefreshTone(email, refreshToken);
        if (!user.isPresent()) {
            throw new AppException("Refesh Token false");
        }
        AuthResponse accessToken = new AuthResponse();
        accessToken.setAccessToken(this.tokenService.createAccessToken(email));

        String newRefreshToken = this.tokenService.createAccessToken(email);
        this.userService.updateRefreshToken(email, newRefreshToken);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                // .domain("example.com")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(accessToken);

    }

}