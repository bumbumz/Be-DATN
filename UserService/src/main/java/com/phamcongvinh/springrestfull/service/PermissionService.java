package com.phamcongvinh.springrestfull.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.phamcongvinh.springrestfull.module.Permission;
import com.phamcongvinh.springrestfull.module.dto.Request.Meta;
import com.phamcongvinh.springrestfull.module.dto.Request.RequestPermissionUpdate;
import com.phamcongvinh.springrestfull.module.dto.Request.ResultPaginationDTO;
import com.phamcongvinh.springrestfull.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;

    }

    public Permission createPermission(Permission param) {
        return this.permissionRepository.save(param);
    }

    public Permission updatePermission(Permission param) {
        Permission checkById = this.checkById(param.getId()).get();
        if (!param.getApiPath().isEmpty())
            checkById.setApiPath(param.getApiPath());
        if (!param.getMethod().isEmpty())
            checkById.setMethod(param.getMethod());
        if (!param.getModule().isEmpty())
            checkById.setModule(param.getModule());
        this.permissionRepository.save(checkById);
        return checkById;
    }

    public boolean existsByApiPathAndMethodAndModule(Permission param) {

        return this.permissionRepository.existsByApiPathAndMethodAndModule(
                param.getApiPath(),
                param.getMethod(),
                param.getModule());
    }

    public ResultPaginationDTO getFiter(Specification<Permission> spec,
            Pageable pageable) {
        Page<Permission> page = this.permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);// số trang
        meta.setPageSize(pageable.getPageSize());// số lượng phần tửtử

        meta.setPages(page.getTotalPages());// tổng số trang
        meta.setTotal(page.getTotalElements());// tổng số phần tử
        res.setMeta(meta);
        res.setResult(page.getContent());
        return res;
    }

    public void deleteById(Permission param) {
        param.getRoles().forEach(
                item -> item.getPermissions().remove(param));
        this.permissionRepository.delete(param);

    }

    // check====================================================================================
    public Optional<Permission> checkById(long id) {
        return this.permissionRepository.findById(id);
    }
}
