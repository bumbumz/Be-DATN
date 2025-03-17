package com.phamcongvinh.springrestfull.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.phamcongvinh.springrestfull.module.Role;
import com.phamcongvinh.springrestfull.module.User;
import com.phamcongvinh.springrestfull.module.dto.Request.CUser;
import com.phamcongvinh.springrestfull.module.dto.Request.RequestUserUpdate;
import com.phamcongvinh.springrestfull.module.dto.Request.RoleInUser;
import com.phamcongvinh.springrestfull.module.dto.Request.httpCilent.CProfile;
import com.phamcongvinh.springrestfull.module.dto.Response.UserDTO;
import com.phamcongvinh.springrestfull.module.dto.Response.Filter.FilterResponse;
import com.phamcongvinh.springrestfull.module.dto.Response.Filter.Meta;
import com.phamcongvinh.springrestfull.repository.UseRepository;
import com.phamcongvinh.springrestfull.repository.httpClient.ProfileClient;
import com.phamcongvinh.springrestfull.util.exception.AppException;
import com.phamcongvinh.springrestfull.util.exception.IdInvalidException;

@Service
public class UserService {
    private final UseRepository useRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileClient profileClient;
    private final RoleService roleService;

    public UserService(UseRepository useRepository,
            PasswordEncoder passwordEncoder,
            ProfileClient profileClient,
            RoleService roleService) {
        this.useRepository = useRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileClient = profileClient;
        this.roleService = roleService;
    }

    // ======================================================================================
    public UserDTO mapperUserToUserDTO(User param) {
        UserDTO res = new UserDTO(
                param.getId(),
                param.getName(),
                param.getEmail(),
                // param.getAge(),
                // param.getGender(),
                // param.getAddress(),
                param.getCreatedAt(),
                param.getUpdatedAt(),
                param.getCreatedBy(),
                param.getUpdatedBy());
        return res;
    }

    // ===================================================================================================
    public User mapperCUserToUser(CUser param) {
        User res = new User(
                param.getName(),
                param.getEmail(),
                param.getPassword()
        // param.getAge(),
        // param.getGender(),
        // param.getAddress()
        );
        return res;
    }

    public UserDTO convertToResCreateUser(User user) {
        UserDTO resUserDTO = new UserDTO();

        resUserDTO.setId(user.getId());
        resUserDTO.setName(user.getName());

        resUserDTO.setEmail(user.getEmail());

        resUserDTO.setCreatedAt(user.getCreatedAt());
        resUserDTO.setCreatedBy(user.getCreatedBy());

        resUserDTO.setRole(
                user.getRole() != null
                        ? new RoleInUser(user.getRole().getId(), user.getRole().getName())
                        : null);

        return resUserDTO;

    }

    // ===================================================================================================
    public UserDTO createUser(CUser param) {
        param.setPassword(passwordEncoder.encode(param.getPassword()));
        User nUser = this.mapperCUserToUser(param);
        User user = this.useRepository.save(nUser);
        UserDTO res = this.mapperUserToUserDTO(user);
        CProfile cProfile = new CProfile(param.getFirstName(), param.getLastName(), user.getId());
        this.profileClient.creatProfile(cProfile);

        return res;
    }

    public void updateRefreshToken(String email, String resfreshToken) {
        User user = this.checkEmail(email).get();
        user.setRefreshToken(resfreshToken);
        this.useRepository.save(user);
    }

    public FilterResponse filterUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.useRepository.findAll(spec, pageable);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        FilterResponse res = new FilterResponse();
        res.setMeta(meta);
        List<UserDTO> listUser = pageUser.getContent().stream()
                .map(
                        item -> this.mapperUserToUserDTO(item))
                .collect(Collectors.toList());
        res.setObject(listUser);
        return res;
    }

    public UserDTO updateUser(RequestUserUpdate param) throws IdInvalidException {

        Optional<User> user = this.checkId(param.getId());
        if (!user.isPresent()) {
            throw new IdInvalidException("Không tìm thấy user tồn tại " + param.getId() + "này .");
        }
        if (param.getName() != null) {
            user.get().setName(param.getName());
        }

        if (param.getRole() != null) {
            Optional<Role> checkId = this.roleService.checkId(param.getRole().getId());

            user.get().setRole(checkId.isPresent() ? checkId.get() : null);
        }
        this.useRepository.save(user.get());
        UserDTO resUserDTO = this.convertToResCreateUser(user.get());
        return resUserDTO;

    }

    public void deleteUser(long id) throws IdInvalidException {

        this.useRepository.deleteById(id);

    }

    // =====================================================================================
    public Optional<User> checkEmail(String email) {
        return this.useRepository.findByEmail(email);
    }

    public Optional<User> checkId(long id) {
        return this.useRepository.findById(id);
    }

    public Optional<User> checkEmailAndRefreshTone(String email, String refreshToken) {
        return this.useRepository.findByEmailAndRefreshToken(email, refreshToken);
    }

}
