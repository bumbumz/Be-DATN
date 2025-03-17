package com.phamcongvinh.springrestfull.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.phamcongvinh.springrestfull.module.Permission;
import com.phamcongvinh.springrestfull.module.Role;
import com.phamcongvinh.springrestfull.module.User;
import com.phamcongvinh.springrestfull.repository.PermissionRepository;
import com.phamcongvinh.springrestfull.repository.RoleRepository;
import com.phamcongvinh.springrestfull.repository.UseRepository;
import com.phamcongvinh.springrestfull.util.constrant.EnumGender;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UseRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UseRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("INIT DATABESE");
        long countUser = this.userRepository.count();
        long countPer = this.permissionRepository.count();
        long countRole = this.roleRepository.count();

        if (countPer == 0) {
            List<Permission> arr = new ArrayList<>();

            arr.add(new Permission("Get by id user", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get filter user", "/api/v1/users", "GET", "USERS"));
            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Check account", "/api/v1/users/auth/check-account", "POST", "ALL"));
            arr.add(new Permission("Get my account", "/api/v1/users/auth/account", "GET", "ALL"));
            arr.add(new Permission("Get permison", "/api/v1/users/permissions", "GET", "PERMISSION"));
            arr.add(new Permission("Create permison", "/api/v1/users/permissions", "POST", "PERMISSION"));

            this.permissionRepository.saveAll(arr);
        }
        if (countRole == 0) {
            List<Permission> arrPer = this.permissionRepository.findAll();
            Role role = new Role();
            role.setName("SUPER_ADMIN");
            role.setDescription("admin");
            role.setActive(true);
            role.setPermissions(arrPer);
            this.roleRepository.save(role);
        }
        if (countUser == 0) {
            User user = new User();
            user.setName("admin");
            user.setEmail("admin@gmail.com");
            user.setPassword(passwordEncoder.encode("123456"));
            // user.setAddress("hn");
            // user.setAge(25);
            // user.setGender(EnumGender.NAM);
            Optional<Role> adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole.isPresent()) {
                user.setRole(adminRole.get());
            }
            this.userRepository.save(user);

        }
        if (countRole > 0 && countPer > 0 && countUser > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");

    }
}
