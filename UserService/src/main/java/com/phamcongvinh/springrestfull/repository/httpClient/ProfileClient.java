package com.phamcongvinh.springrestfull.repository.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.phamcongvinh.springrestfull.module.dto.Request.httpCilent.CProfile;

@FeignClient(value = "profile-service", url = "${spring.service.profile}")
public interface ProfileClient {

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    Object creatProfile(CProfile cProfile);

}