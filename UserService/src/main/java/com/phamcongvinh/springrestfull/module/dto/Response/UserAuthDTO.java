package com.phamcongvinh.springrestfull.module.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthDTO {
    private long id;

    private String name;

    private String email;

}
