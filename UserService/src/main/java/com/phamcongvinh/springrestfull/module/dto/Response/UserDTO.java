package com.phamcongvinh.springrestfull.module.dto.Response;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.phamcongvinh.springrestfull.module.dto.Request.RoleInUser;
import com.phamcongvinh.springrestfull.util.constrant.EnumGender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserDTO {
    private long id;

    private String name;

    private String email;

    // private int age;

    // private EnumGender gender;
    // private String address;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
     private RoleInUser role;
    public UserDTO(long id, String name, String email, Instant createdAt, Instant updatedAt, String createdBy,
            String updatedBy) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }


}
