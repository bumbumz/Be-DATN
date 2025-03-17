package com.phamcongvinh.springrestfull.module.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

}
