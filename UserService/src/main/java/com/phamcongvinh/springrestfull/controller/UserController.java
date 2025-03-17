package com.phamcongvinh.springrestfull.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phamcongvinh.springrestfull.module.User;
import com.phamcongvinh.springrestfull.module.dto.Request.CUser;
import com.phamcongvinh.springrestfull.module.dto.Request.RequestUserUpdate;
import com.phamcongvinh.springrestfull.module.dto.Response.UserDTO;
import com.phamcongvinh.springrestfull.module.dto.Response.Filter.FilterResponse;
import com.phamcongvinh.springrestfull.service.UserService;
import com.phamcongvinh.springrestfull.util.annotation.AppMessage;
import com.phamcongvinh.springrestfull.util.exception.AppException;
import com.phamcongvinh.springrestfull.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/auth")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CUser param) throws AppException {
        Optional<User> checkEmail = this.userService.checkEmail(param.getEmail());
        if (checkEmail.isPresent()) {
            throw new AppException("Email đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUser(param));

    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable("id") long id) throws AppException {
        Optional<User> checkid = this.userService.checkId(id);
        if (!checkid.isPresent()) {
            throw new AppException("Không tìm thấy user tồn tại " + id + "này .");
        }
        UserDTO res = this.userService.mapperUserToUserDTO(checkid.get());
        return ResponseEntity.ok().body(res);

    }

    @GetMapping("/users")
    public ResponseEntity<FilterResponse> filterUser(
            @Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.userService.filterUser(spec, pageable));
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody RequestUserUpdate param) throws IdInvalidException {

        UserDTO users = userService.updateUser(param);
        return ResponseEntity.ok().body(users);

    }

    @DeleteMapping("/users/{id}")
    @AppMessage("Xóa người dùng thành công")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<User> checkid = this.userService.checkId(id);
        if (!checkid.isPresent()) {
            throw new IdInvalidException("Không tìm thấy user tồn tại " + id + "này .");
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().body(null);
    }

}
