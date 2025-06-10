package com.oilerrig.vendor.controller;

import com.oilerrig.vendor.auth.ApiAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class AuthController {


    private final ApiAuthenticationService apiAuthenticationService;

    AuthController(ApiAuthenticationService apiAuthenticationService) {
        this.apiAuthenticationService = apiAuthenticationService;
    }

    @GetMapping("/caches/init")
    public ResponseEntity<String> refreshUserCache() {
        apiAuthenticationService.updateKeyCache();
        return ResponseEntity.ok().body("Successfully Updated Caches.");
    }
}
