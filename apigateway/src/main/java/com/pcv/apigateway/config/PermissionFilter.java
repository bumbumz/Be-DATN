package com.pcv.apigateway.config;

import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcv.apigateway.dto.ApiResponse;
import com.pcv.apigateway.dto.CheckUser;

import reactor.core.publisher.Mono;

@Component
public class PermissionFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;

    @Autowired
    public PermissionFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build(); // Gọi User Service
    }

    private static final String EXCLUDED_PREFIX = "/api/v1/users/auth";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
       // String path = request.getPath().toString();
        String path = normalizePath(request.getPath().toString()); // Chuẩn hóa đường dẫn
        String httpMethod = request.getMethod().name();
        System.out.println("path:" + path);
        System.out.println("httpMethod:" + httpMethod);

       
        // Tạo request kiểm tra quyền
        if (path.startsWith(EXCLUDED_PREFIX)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing or Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        return webClient.post()
                .uri("/api/v1/users/auth/check-account")
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .bodyValue(new CheckUser(path, httpMethod))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Boolean>>() {
                })
                .map(ApiResponse::getData)
                .defaultIfEmpty(false)
                .flatMap(isAllowed -> {
                    if (!isAllowed) {
                        return onError(exchange, "Forbidden", HttpStatus.FORBIDDEN);
                    }
                    return chain.filter(exchange);
                });
    }

    private String normalizePath(String path) {
        return path.replaceAll("/\\d+", "/{id}"); // Thay số bằng {id}
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Tạo JSON phản hồi
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("statusCode", status.value());
        errorResponse.put("error", errorMessage);
        errorResponse.put("message", "Token không hợp lệ hoặc api không được phép try cập");

        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1; // Chạy filter trước tất cả các request khác
    }
}
