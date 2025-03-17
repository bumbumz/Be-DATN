package com.phamcongvinh.springrestfull.module.dto.Request.httpCilent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CProfile {
    private String firstName;
    private String lastName;
    private long userId;

}
