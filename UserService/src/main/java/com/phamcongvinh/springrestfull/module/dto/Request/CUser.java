package com.phamcongvinh.springrestfull.module.dto.Request;

import com.phamcongvinh.springrestfull.util.constrant.EnumGender;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CUser {
    private String name;
    private String email;
    private String password;
    // private int age;
    // private EnumGender gender;
    // private String address;
    private String firstName;
    private String lastName;
}
