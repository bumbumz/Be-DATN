package com.phamcongvinh.springrestfull.module.dto.Request;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class RequestPermissionUpdate {
    private long id;

    private String apiPath;
    private String method;
    private String module;
    private String name;
    
}
