package com.phamcongvinh.springrestfull.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.phamcongvinh.springrestfull.module.Permission;
import com.phamcongvinh.springrestfull.module.dto.Request.RequestPermissionUpdate;
import com.phamcongvinh.springrestfull.service.PermissionService;
import com.phamcongvinh.springrestfull.util.exception.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

@RestController
@RequestMapping("/api/v1/users")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;

    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission param) throws IdInvalidException {

        if (this.permissionService.existsByApiPathAndMethodAndModule(param)) {
            throw new IdInvalidException("premission đã tồn tại");
        }
        return ResponseEntity.ok().body(this.permissionService.createPermission(param));

    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> updatePermission(@RequestBody Permission param) throws IdInvalidException {
        Optional<Permission> checkById = this.permissionService.checkById(param.getId());
        if ((param.getApiPath().isEmpty()) || (param.getModule().isEmpty()) || (param.getMethod().isEmpty())) {
            throw new IdInvalidException("yêu cầu đầy đủ các thông tin apiPath, method, module");
        }
        if (!checkById.isPresent()) {
            throw new IdInvalidException("Id không tồn tại");
        }
        if (this.permissionService.existsByApiPathAndMethodAndModule(param)) {
            throw new IdInvalidException("premission đã tồn tại");
        }
        return ResponseEntity.ok().body(this.permissionService.updatePermission(param));

    }

    @GetMapping("/permissions")
    public ResponseEntity<?> filterPermission(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.getFiter(spec, pageable));

    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Permission> checkById = this.permissionService.checkById(id);
        if (!checkById.isPresent()) {
            throw new IdInvalidException("Id không tồn tại");
        }
        this.permissionService.deleteById(checkById.get());
        return ResponseEntity.ok().body(null);
    }

}
