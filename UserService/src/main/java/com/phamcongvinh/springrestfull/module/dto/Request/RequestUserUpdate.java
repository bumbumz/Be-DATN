package com.phamcongvinh.springrestfull.module.dto.Request;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phamcongvinh.springrestfull.module.Role;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class RequestUserUpdate {
    private long id;

    @NotBlank(message = "Không được để trống name")
    private String name;

    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;

    private Role role;

}
