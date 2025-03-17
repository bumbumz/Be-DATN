package com.phamcongvinh.springrestfull.module.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckUser {
    private String path;
    private String httpMethod;

}
